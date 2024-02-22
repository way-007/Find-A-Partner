package com.way.threes_company_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.way.threes_company_backend.model.domain.UserTeam;
import com.way.threes_company_backend.mapper.UserTeamMapper;
import com.way.threes_company_backend.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author way
* @description 针对表【user_team(用户与队伍的关系表)】的数据库操作Service实现
* @createDate 2024-02-17 17:51:33
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




