package com.nx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.model.domain.UserTeam;
import com.nx.service.UserTeamService;
import com.nx.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 18702
* @description 针对表【user_team(用户 - 队伍表)】的数据库操作Service实现
* @createDate 2024-03-08 22:31:50
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




