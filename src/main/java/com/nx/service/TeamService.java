package com.nx.service;

import com.nx.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.model.domain.User;
import com.nx.model.dto.TeamQuery;
import com.nx.model.request.TeamJoinRequest;
import com.nx.model.request.TeamQuitRequest;
import com.nx.model.request.TeamUpdateRequest;
import com.nx.model.vo.TeamUserVO;

import java.util.List;

/**
 * @author 18702
 * @description 针对表【team(队伍)】的数据库操作Service
 * @createDate 2024-03-09 00:42:53
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team      队伍信息
     * @param loginUser 当前登录的用户
     * @return long
     * @author nx
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @return {@link List }<{@link TeamUserVO }>
     * @author nx
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);


    /**
     * 更新队伍
     *
     * @param teamUpdateRequest team update request
     * @param loginUser         login user
     * @return boolean
     * @author nx
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest team join request
     * @param loginUser       login user
     * @return boolean
     * @author nx
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest team quit request
     * @param loginUser       login user
     * @return boolean
     * @author nx
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     *
     * @param teamId    team id
     * @param loginUser login user
     * @return boolean
     * @author nx
     */
    boolean deleteTeam(long teamId, User loginUser);
}
