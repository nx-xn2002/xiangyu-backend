package com.nx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.common.ErrorCode;
import com.nx.exception.BusinessException;
import com.nx.mapper.TeamMapper;
import com.nx.model.domain.Team;
import com.nx.model.domain.User;
import com.nx.model.domain.UserTeam;
import com.nx.model.dto.TeamQuery;
import com.nx.model.enums.TeamStatusEnum;
import com.nx.model.request.TeamJoinRequest;
import com.nx.model.request.TeamQuitRequest;
import com.nx.model.request.TeamUpdateRequest;
import com.nx.model.vo.TeamUserVO;
import com.nx.model.vo.UserVO;
import com.nx.service.TeamService;
import com.nx.service.UserService;
import com.nx.service.UserTeamService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author 18702
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3. 校验信息
        //   1. 队伍人数 > 1且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        //   2. 队伍名称 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        //   3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(name) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //   4. status是否为公开，不传默认为0（公开）
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        //   5. 如果status是1（加密），需要传密码，密码 <=32
        String password = team.getPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不符合要求");
            }
        }
        //   6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime == null || new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间设置不符合要求");
        }
        //   7. 校验用户最多创建5个队伍
        //TODO:有BUG用户可能一次性多次创建
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.NO_AUTH, "用户最多创建5个队伍");
        }
        //4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean result = this.save(team);
        if (!result || team.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        //5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(loginUser.getId());
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamQuery.getSearchText();
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id", userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        //不展示已过期队伍
        queryWrapper.and(qw -> qw.gt("expire_time", new Date())).or().isNull("expire_time");

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            if (user == null) {
                continue;
            }
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //脱敏用户信息
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamById(id);
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword()) && StringUtils.isBlank(oldTeam.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须传入密码");
            }
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);
        return this.updateById(team);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有的队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "密码错误");
            }
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        userTeamQueryWrapper.eq("team_id", teamId);
        long userHasJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (userHasJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入队伍");
        }
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建和加入5个队伍");
        }

        long teamHasJoinCount = countTeamUserByTeamId(teamId);
        if (teamHasJoinCount >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
        }
        //插入user-team表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setUserId(userId);
        queryUserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamUserCount = this.countTeamUserByTeamId(teamId);
        //如果队伍只剩1人，解散
        if (teamUserCount == 1) {
            //删除队伍和所有加入队伍的关系
            this.removeById(teamId);
            userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamId);
            userTeamQueryWrapper.eq("user_id", userId);
            return userTeamService.remove(userTeamQueryWrapper);
        } else {
            //判断是否是队长
            //如果是队长，将权限转移给最早加入的用户
            if (team.getUserId().equals(userId)) {
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("team_id", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> list = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(list) || list.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = list.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setUserId(nextTeamLeaderId);
                updateTeam.setId(teamId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
            userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("user_id", userId);
            userTeamQueryWrapper.eq("team_id", teamId);
            return userTeamService.remove(userTeamQueryWrapper);
        }
    }

    /**
     * 根据队伍id获取队伍信息
     *
     * @param teamId team id
     * @return {@link Team }
     * @author nx
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        //1. 校验请求参数
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 校验队伍是否存在
        Team team = this.getTeamById(teamId);
        //3. 校验你是否是队伍的队长
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        //4. 移除所有加入关系
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        //5. 移除队伍
        return this.removeById(teamId);
    }

    private long countTeamUserByTeamId(Long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper;
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}




