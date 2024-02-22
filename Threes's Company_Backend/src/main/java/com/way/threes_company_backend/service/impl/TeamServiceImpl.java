package com.way.threes_company_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.way.threes_company_backend.common.ErrorCode;
import com.way.threes_company_backend.dto.TeamQuery;
import com.way.threes_company_backend.exception.BusinessResponseException;
import com.way.threes_company_backend.model.domain.Team;
import com.way.threes_company_backend.mapper.TeamMapper;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.model.domain.UserTeam;
import com.way.threes_company_backend.model.domain.enums.StatusEnums;
import com.way.threes_company_backend.model.domain.request.JoinParams;
import com.way.threes_company_backend.model.domain.request.UpdateTeamRequestParams;
import com.way.threes_company_backend.model.domain.vo.TeamUserVO;
import com.way.threes_company_backend.model.domain.vo.UserVO;
import com.way.threes_company_backend.service.TeamService;
import com.way.threes_company_backend.service.UserService;
import com.way.threes_company_backend.service.UserTeamService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author way
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-02-17 17:51:20
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;


    @Override
    @Transactional(rollbackFor = Exception.class)   // 事务注解 rollbackfor指定出异常之后应该怎么办（抛出异常）
    public Integer save(Team team, User loginUser) {
//        1. 请求参数是否为空
        if(team == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
//        2. 是否登录，未登录不允许创建
        if(loginUser == null) {
            throw new BusinessResponseException(ErrorCode.NO_LOGIN);
        }
        final int userId = loginUser.getId();
//        3. 校验信息
//                - 队伍人数 > 1  &&  <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum < 1 || maxNum > 20) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍人数不正确");
        }
//                - 队伍标题 <= 20
        if(StringUtils.isBlank(team.getName()) || team.getName().length() > 20) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍标题不正确");
        }
//                - 描述 <= 512   可以为空
        if(StringUtils.isNotBlank(team.getDescription()) && team.getDescription().length() > 512) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍描述不正确");
        }
//                - status 是否公开  不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);  // 传递一个默认值为0如果用户没有传递的话
        StatusEnums statusEnums = StatusEnums.getEnumsByValue(status);
        if(statusEnums == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍状态不正确");
        }
//        - status是加密状态，一定要有密码  <= 32
        if(statusEnums.equals(StatusEnums.SECRET)) {
            if(StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 32) {
                throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "加密状态下的密码不正确");
            }
        }
//                - 超时时间 > 当前时间
        if(new Date().after(team.getExpireTime())) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍创建超时");
        }
//                - 校验用户最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamCounts = this.count(queryWrapper);
        if(hasTeamCounts >= 5) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "用户创建队伍数量过多>5");
        }
//        4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        int teamId = team.getId();
        // 测试事务是否执行会进行回滚   事务只要执行了就会回滚，不会进行任何下面对数据库进行数据插入的操作
        // 这里使用事务的目的就是为了使两个对数据库进行操作的代码都必须同时执行成功，只要有一个没有成功执行就进行事务的回滚
//        if(true) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "事务回滚执行");  // 插入失败之后事务就会进行自动回滚
//        }
        if(!save || teamId < 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "插入队伍表失败");  // 插入失败之后事务就会进行自动回滚
        }
//        5. 插入用户 >  队伍关系表  这里还需要插入数据到用户-队伍关系表里面所以就需要用到事务   使用事务注解Transactional

        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if(!save) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "插入用户队伍关系表失败");  // 插入失败之后事务就会进行自动回滚
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 01. 组合好所有的查询条件  根据传递的查询参数
        if(teamQuery != null) {
            // 将需要用到的查询条件全部取出来，然后进行判断 + 查询
            String name = teamQuery.getName();
            // 根据队伍名称查询
            if(StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            // 根据teamid来查询队伍信息
            List<Integer> idList = teamQuery.getIdList();
            if(CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchKey = teamQuery.getSearchKey();
            // 根据关键词查询队伍名称和描述  两个字段里面都能进行查询
            if(StringUtils.isNotBlank(searchKey)) {
                queryWrapper.and(qw -> qw.like("name", searchKey).or().like("description", searchKey));
            }
            Integer id = teamQuery.getId();
            // 根据队伍id查询
            if(id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String description = teamQuery.getDescription();
            // 根据队伍描述查询
            if(StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            // 根据队伍状态查询 0-公开 1-私有 2-加密  只有管理员才能查看非公开的队伍： 私有 、 加密
            Integer status = teamQuery.getStatus();
            StatusEnums statusEnums = StatusEnums.getEnumsByValue(status);
            if(statusEnums == null) {
                statusEnums = StatusEnums.PUBLIC;  // 如果没有就默认查询公开的
            }
            if(!isAdmin && !statusEnums.equals(StatusEnums.PRIVATE)) {
                throw new BusinessResponseException(ErrorCode.NO_AUTHOR);
            }
            queryWrapper.eq("status", statusEnums.getValue());
            Integer maxNum = teamQuery.getMaxNum();
            // 根据队伍最大人数查询
            if(maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Integer userId = teamQuery.getUserId();
            // 根据创建人的id进行查询
            if(userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
        }
        // 02. 不展示过期队伍
            // and expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        // 03. 开始查询数据
        List<Team> teamList = this.list(queryWrapper);

        // 04. 根据上面查询的队伍数据关联查询用户信息
            // 1. 查询队伍创建人的信息
            // select * from team t left join  user_team ut on t.id = ut.teamId;
            // 2. 查询加入队伍的所用用户信息
            // select * from team t left join  user_team ut on t.id = ut.teamId left join user u on t.userId = u.id;
        if(CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        // 05. 新建一个封装的VO集合
        List<TeamUserVO> teamUserVOList = new ArrayList<>();

        // 06. 遍历teamList里面的所用用户id然后通过userService接口查询用户信息
        for (Team team : teamList) {
            Integer userId = team.getUserId();
            User user = userService.getById(userId);
            // 脱敏team 的信息
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏user 的信息
            if(user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUserVO(userVO);
            }
            // 将查询到的脱敏后的用户信息塞入vo集合中
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(UpdateTeamRequestParams updateTeamRequestParams, User loginUser) {
        // 01. 判断是否为空
        if(updateTeamRequestParams == null) {
            throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS);
        }
        // 02. 查询队伍是否存在
        int id = updateTeamRequestParams.getId();
        if(id < 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "没有该队伍信息");
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null) {
            throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS, "队伍不存在");
        }
        // 03. 如果更改为加密必须设置密码
        StatusEnums statusEnums = StatusEnums.getEnumsByValue(updateTeamRequestParams.getStatus());
        if(statusEnums.equals(StatusEnums.SECRET)) {
            if(StringUtils.isBlank(updateTeamRequestParams.getPassword())) {
                throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "加密队伍必须设置队伍密码");
            }
        }
        // 03. 只有管理员或者创建者可以修改
        long userId = oldTeam.getUserId();
        long id1 = loginUser.getId();
        if(userId != id1 && !userService.isAdmin(loginUser)) {
            throw new BusinessResponseException(ErrorCode.NO_AUTHOR);
        }
        // 04. 创建一个team 将传递过来的team复制到原始的team中
        Team team = new Team();
        BeanUtils.copyProperties(updateTeamRequestParams, team);
        return this.updateById(team);
    }

    @Override
    public boolean joinTeam(JoinParams joinParams, User loginUser) {
        // 01. 判空
        if(joinParams == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        Team team = this.getById(joinParams.getId());  // 根据传递的id获取数据库里面的队伍信息
        if(team == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍不存在");
        }

        // 02. 不能加入私有队伍
        StatusEnums statusEnums = StatusEnums.getEnumsByValue(team.getStatus());
        if(StatusEnums.PRIVATE.equals(statusEnums)) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "不能加入私有队伍");
        }


        RLock lock = redissonClient.getLock("way:user:joinTeam:lock");
        try {
            while (true) {
                if(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                    System.out.println("get lock" + Thread.currentThread().getId());
                    // 03. 遇到加密队伍必须密码匹配
                    if(StatusEnums.SECRET.equals(statusEnums)) {
                        if(joinParams.getPassword() == null || !joinParams.getPassword().equals(team.getPassword())) {
                            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "密码不匹配加入队伍失败");
                        }
                    }
                    // 04. 不能加入已经过期的队伍
                    if (team.getExpireTime().before(new Date())) {
                        throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍已经过期不能加入");
                    }
                    // 05. 最多加入5个队伍
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", loginUser.getId());
                    long hasJoinTeam = userTeamService.count(queryWrapper); // 调用查询用户队伍关系表里面的数据
                    if(hasJoinTeam >= 5) {
                        throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "最对只能创建或加入5个队伍");
                    }
                    // 06. 不能重复加入已经加入的队伍
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", loginUser.getId());
                    queryWrapper.eq("teamId", team.getId());
                    long hasJoinCommonTeam = userTeamService.count(queryWrapper); // 调用查询用户队伍关系表里面的数据
                    if(hasJoinCommonTeam > 0) {
                        throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "不能重复加入队伍");
                    }
                    // 07. 不能加入已满的队伍
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("teamId", team.getId());
                    long alreadyCount = userTeamService.count(queryWrapper);
                    if(alreadyCount >= team.getMaxNum()) {
                        throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍已经满不能加入新人");
                    }
                    // 08. 新建用户队伍关系表信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(loginUser.getId());
                    userTeam.setTeamId(team.getId());
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (Exception e) {
            log.error("trylock is error", e);
            return false;
        } finally {   // 释放锁必须写在finally里面  因为如果前面的代码报错了就不会执行释放锁   所以必须让锁过期
            if(lock.isHeldByCurrentThread()) {
                System.out.println("forgive lock" + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 不管是运行时异常还是编译的异常都会触发事务的回滚
    public boolean liveTeam(long id, User loginUser) {
        // 01. 判空
        if(id <= 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        final int userId = loginUser.getId();
        // 02. 获取队伍人数
        Team team = this.getById(id);
        if(team == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍不存在");
        }
        // 03. 校验我是否加入该队伍
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(team.getId());
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        // 加入该队伍的关系对象
//        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userId", userId);
//        queryWrapper.eq("teamId", team.getId());
        long alreadyCount = userTeamService.count(queryWrapper);
        if(alreadyCount != 1) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "您未加入该队伍，退出无效");
        }

        // 04. 获取队伍人数
        QueryWrapper<UserTeam> userTeamCount = new QueryWrapper<>();
        userTeamCount.eq("teamId", team.getId());
        long count = userTeamService.count(userTeamCount);  // 这里的querywrapper对象应该是新建的查询用户队伍关系的querywarpper
        // 02-1 如果只有一个人  队伍解散  isDelete -> 0(会自动将其改为1 不会是直接删除)  + 将用户队伍关系表里面进行清除
        if(count == 1) {
            // isdelete 改为0
            this.removeById(team.getId());
            // 用户队伍关系表清除   这里不能要 否则就移除了两次关系导致
//            return userTeamService.remove(queryWrapper);  // 移除用户队伍关系表里面的所有teamID的数据
        }else {
            // 队长退出  权限转移
            if(userId == team.getUserId()) {
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", team.getId());
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                // 判空
                if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "没有可以转让的队友");
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Integer nextUserTeamUserId = nextUserTeam.getUserId();
                // 更改当前队伍的id为新的用户id
                Team updateTeam = new Team();
                updateTeam.setId(team.getId());
                updateTeam.setUserId(nextUserTeamUserId);
                boolean result = this.updateById(updateTeam);
                if(!result) {
                    throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "转让队长身份失败");
                }
            }
        }
        // 移除关系表   这里抽出来了三个地方  不是队长需要移除  只有一人也需要移除
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 不管是运行时异常还是编译的异常都会触发事务的回滚
    public boolean deleteTeam(long id, User loginUser) {
        // 01. 校验是否参数存在
        if(id <= 0 ) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 校验是否队伍存在
        Team team = this.getById(id);
        if(team == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "队伍不存在");
        }
        final int teamId = team.getId();
        final int userId = loginUser.getId();
        // 03. 校验是否当前用户为队长
        if(team.getUserId() != userId) {
            throw new BusinessResponseException(ErrorCode.NO_AUTHOR, "非队长，没有权限");
        }
        // 04. 移除关联关系 和队伍表信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
            // 这里涉及到两个表的操作  必须都执行成功 不能一个成功一个失败  所以需要用到事务
        boolean result = userTeamService.remove(queryWrapper);
        if(!result) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "删除队伍关联关系失败");
        }
        return this.removeById(teamId);
    }

//    @Override
//    public List<Team> hadJoinTeam(User loginUser) {
//        // 01. 校验是否存在
//        if(loginUser == null) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
//        }
//        // 02. 获取用户id
//        final long userId = loginUser.getId();
//        // 03. 查询关系表
//        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userId", userId);
//        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
//        // 04. 遍历里面的teamId 然后再去查找所有的队伍信息
//        List<Team> teamList = new ArrayList<>();
//        queryWrapper = new QueryWrapper<>();
////        for (UserTeam userTeam : userTeamList) {
////            queryWrapper.eq("teamId",userTeam.getTeamId());
//////            Team one = this.getOne(queryWrapper);
////        }
//
//        return null;
//    }

//    @Override
//    public List<Team> hadCreatedTeam(User loginUser) {
//        // 01. 校验是否存在
//        if(loginUser == null) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
//        }
//        // 02. 获取用户id
//        final long userId = loginUser.getId();
//        // 03. 直接查询
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userId", userId);
//        List<Team> list = this.list(queryWrapper);
//        if(list == null) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "您没有创建任何队伍");
//        }
//        return list;
//    }
}




