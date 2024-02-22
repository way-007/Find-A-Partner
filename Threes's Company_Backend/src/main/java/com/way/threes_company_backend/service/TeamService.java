package com.way.threes_company_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.way.threes_company_backend.dto.TeamQuery;
import com.way.threes_company_backend.model.domain.Team;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.model.domain.request.JoinParams;
import com.way.threes_company_backend.model.domain.request.UpdateTeamRequestParams;
import com.way.threes_company_backend.model.domain.vo.TeamUserVO;

import java.util.List;

/**
* @author way
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-02-17 17:51:20
*/
public interface TeamService extends IService<Team> {

    /**
     * 新增队伍表的方法实现
     * @param team  传入一个封装类的team对象
     * @param loginUser  传入登录之后的用户信息
     * @return  返回一个整型的用户id
     */
    Integer save(Team team, User loginUser);


    /**
     * 查询队伍信息
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新用户信息
     * @param updateTeamRequestParams
     * @param loginUser
     * @return
     */
    boolean updateTeam(UpdateTeamRequestParams updateTeamRequestParams, User loginUser);

    /**
     * 加入队伍
     * @param joinParams
     * @param loginUser
     * @return
     */
    boolean joinTeam(JoinParams joinParams, User loginUser);

    /**
     * 用户退出队伍（队长或队员）
     * @param id
     * @param loginUser
     */
    boolean liveTeam(long id, User loginUser);


    /**
     * 队长解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

//    /**
//     * 获取当前用户已经加入的队伍信息
//     * @param loginUser
//     * @return
//     */
//    List<Team> hadJoinTeam(User loginUser);
//
//
//    /**
//     * 获取当前用户已经创建的队伍信息
//     * @param loginUser
//     * @return
//     */
//    List<Team> hadCreatedTeam(User loginUser);
}
