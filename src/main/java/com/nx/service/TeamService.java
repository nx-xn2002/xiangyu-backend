package com.nx.service;

import com.nx.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.model.domain.User;

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
}
