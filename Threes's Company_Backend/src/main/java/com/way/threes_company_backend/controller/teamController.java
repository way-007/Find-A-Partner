package com.way.threes_company_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.way.threes_company_backend.common.BaseResponse;
import com.way.threes_company_backend.common.ErrorCode;
import com.way.threes_company_backend.common.QuietAndDelRequest;
import com.way.threes_company_backend.common.ResponseUtils;
import com.way.threes_company_backend.dto.TeamQuery;
import com.way.threes_company_backend.exception.BusinessResponseException;
import com.way.threes_company_backend.model.domain.Team;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.model.domain.UserTeam;
import com.way.threes_company_backend.model.domain.request.JoinParams;
import com.way.threes_company_backend.model.domain.request.TeamRequestParams;
import com.way.threes_company_backend.model.domain.request.UpdateTeamRequestParams;
import com.way.threes_company_backend.model.domain.vo.TeamUserVO;
import com.way.threes_company_backend.service.TeamService;
import com.way.threes_company_backend.service.UserService;
import com.way.threes_company_backend.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController   // 返回的数据格式为json
@RequestMapping("/team")  // 请求的根路径
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
public class teamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;


    /**
     * 增
     * @param teamRequest  传递一个不是完整的team对象作为参数  只需要name  description password status
     * @return  返回一个int类型的id 表示用户创建成功
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addTeam(@RequestBody TeamRequestParams teamRequest, HttpServletRequest request) {
        // 01. 判空
        if(teamRequest == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "没有传递正确的参数");
        }
        // 02. 获取用户信息 通过request请求
        User loginUser = userService.getLoginUser(request);
        // 02. 调用方法存储   调用接口里面的save方法来向数据库里面插入数据 （接口是继承的它的实现类  实现类里面实现了所有的增删改查方法)
        Team team = new Team();
        BeanUtils.copyProperties(teamRequest, team);
        int teamId = teamService.save(team, loginUser);
        // 03. 判断是否插入成功
//        if(!save) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "插入失败");
//        }
        // 04. 返回值为成功插入到数据表中的team对象的id
        return ResponseUtils.success(teamId);
    }



    /**
     * 用户加入的接口
     * @param joinParams  传递一个完整的team对象作为参数
     * @return  只要if判断没有通过就是删除失败
     */
    @PostMapping("/join")
    // 封装一个请求参数类 不能让用户修改所有的字段 必须进行限定
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinParams joinParams, HttpServletRequest request) {
        // 01. 判空
        if(joinParams == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        User loginUser = userService.getLoginUser(request);
        // 02. 调用方法加入队伍
        boolean save = teamService.joinTeam(joinParams, loginUser);
        return ResponseUtils.success(save);
    }

    /**
     * 获取当前用户已加入的所有队伍信息的接口
     * @param request  传递一个request作为参数  当前登录的用户信息
     * @return  只要if判断没有通过就是删除失败
     */
    @GetMapping("/list/my/create")
    // 封装一个请求参数类 不能让用户修改所有的字段 必须进行限定
    public BaseResponse<List<TeamUserVO>> listCreatedTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 获取登录的用户信息
        User loginUser = userService.getLoginUser(request);
        // 03. 将查询封装类里面设置为登录的用户id  就可以直接进行复用
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResponseUtils.success(teamList);
    }


    /**
     * 查：  分页查询  使用list
     * @param teamQuery  单独封装的一个请求参数包装类  里面丢掉了不需要的字段参数
     * @return  只要if判断没有通过就是删除失败
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 获取登录的用户信息
        User loginUser = userService.getLoginUser(request);
        // 03. 查询关系表里面我加入的队伍id
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);

        Map<Integer, List<UserTeam>> mapList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Integer> idList = new ArrayList<>(mapList.keySet());

        teamQuery.setIdList(idList);

        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResponseUtils.success(teamList);
    }

    /**
     * 用户退出队伍的接口
     * @param quietAndDelRequest  需要退出的队伍的用户id
     * @return  只要if判断没有通过就是删除失败
     */
    @PostMapping("/live")
    // 封装一个请求参数类 不能让用户修改所有的字段 必须进行限定
    public BaseResponse<Boolean> liveTeam(@RequestBody QuietAndDelRequest quietAndDelRequest, HttpServletRequest request) {
        // 01. 判空
        if(quietAndDelRequest == null || quietAndDelRequest.getId() < 0) {
           throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
       }
        long id = quietAndDelRequest.getId();
       // 02. 调用service接口方法
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.liveTeam(id, loginUser);
        return ResponseUtils.success(result);
    }


    /**
     * 队长解散队伍的接口
     * @param quietAndDelRequest  需要解散队伍的用户id
     * @return
     */
    @PostMapping("/delete")
    // 封装一个请求参数类 不能让用户修改所有的字段 必须进行限定
    public BaseResponse<Boolean> deleteTeam(@RequestBody QuietAndDelRequest quietAndDelRequest, HttpServletRequest request) {
        // 01. 判空
        if(quietAndDelRequest == null || quietAndDelRequest.getId() < 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        long id = quietAndDelRequest.getId();
        // 02. 调用service接口方法
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        return ResponseUtils.success(result);
    }



    /**
     * 改
     * @param updateTeamRequestParams  传递一个完整的team对象作为参数
     * @return  只要if判断没有通过就是删除失败
     */
    @PostMapping("/update")
    // 封装一个请求参数类 不能让用户修改所有的字段 必须进行限定
    public BaseResponse<Boolean> updateTeam(@RequestBody UpdateTeamRequestParams updateTeamRequestParams, HttpServletRequest request) {
        // 01. 判空
        if(updateTeamRequestParams == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "没有传递正确的参数");
        }
        User loginUser = userService.getLoginUser(request);
        // 02. 调用方更新数据
        boolean save = teamService.updateTeam(updateTeamRequestParams, loginUser);
        return ResponseUtils.success(save);
    }

    /**
     * 查：  根据id查询单条的用户信息
     * @param id  传递一个完整的team对象作为参数
     * @return  只要if判断没有通过就是删除失败
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(int id) {
        // 01. 判空
        if(id <= 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "没有传递正确的参数");
        }
        // 02. 调用方更新数据
        Team team = teamService.getById(id);
        // 03. 判断是否插入成功
        if(team == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "查询单条数据失败");
        }
        // 04. 返回值为成功插入到数据表中的team对象的id
        return ResponseUtils.success(team);
    }


    /**
     * 查：  分页查询  使用list
     * @param teamQuery  单独封装的一个请求参数包装类  里面丢掉了不需要的字段参数
     * @return  只要if判断没有通过就是删除失败
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 获取登录的用户信息
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        // 03. 将所有的队伍id取出来
        final List<Integer> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 03. 获取当前的用户id到关系表里面查询我加入的队伍信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);      // 只查询在上面的队伍列表里面的id  不查询所有列表的id
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);   // 31 33 34 userteam
            Set<Integer> joinedSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());  // 获取我加入的队伍的id
            // 05. 遍历原始的teamList（包含所有的队伍信息  判断里面的id是否包含当前set集合里面的id
            teamList.forEach(team -> {
                boolean hasJoin = joinedSet.contains(team.getId());   // 和上面的teamid做对比
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {}
        // 04. 创建一个新的查询来查出当前每个队伍中已经加入的用户数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 04-1. 将里面根据队伍id进行用户信息的分组
        Map<Integer, List<UserTeam>> teamJoinUserList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        // 04-2.   a -> user1, user2   b -> user1, user3
        teamList.forEach(team -> {
            // TODO: 2024/2/21   会不会存在一个人都没有？    循环遍历team数据，判断hi否有id存在与当前的这个teamlist里面然后拿出长度   这下就可能为空了  队伍id做对比
            team.setHasJoinNum(teamJoinUserList.getOrDefault(team.getId(), new ArrayList<>()).size());
        });
        return ResponseUtils.success(teamList);
    }


    /**
     * 查：  分页查询  使用page
     * @param teamQuery  单独封装的一个请求参数包装类  里面丢掉了不需要的字段参数
     * @return  只要if判断没有通过就是删除失败
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 创建一个新的team类型的对象
        Team team = new Team();
        // 03. 将teamteamquery 里面只需要的字段属性复制给team  前端只需要返回这几个属性即可
        BeanUtils.copyProperties(teamQuery, team);
        // 04. 调用teamquery里面的num和size方法获取到里面继承的页数和页面数据量
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        // 05. 新建一个分页查询
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResponseUtils.success(resultPage);
    }

}
