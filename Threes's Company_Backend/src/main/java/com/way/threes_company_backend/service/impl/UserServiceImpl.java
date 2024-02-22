package com.way.threes_company_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.way.threes_company_backend.common.ErrorCode;
import com.way.threes_company_backend.exception.BusinessResponseException;
import com.way.threes_company_backend.mapper.UserMapper;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.service.UserService;
import com.way.threes_company_backend.utils.AlgoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.math3.util.Pair;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author way
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-17 21:36:14
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    // 密码加密使用的盐
    private final String SLAT = "way_yylx";

    // 用户的登录态的键
    private final String USER_LOGIN_STATE = "userLoginState";
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 业务类里面如果报错应该是属于异常 不能进行错误类的统一返回  应该使用全局异常处理器进行捕获
     * @param userAccount  账户
     * @param userPassword  密码
     * @param checkPassword  确认密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 01. 校验
            // 1. 是否为空
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new  BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
            // 2. 长度是否小于4 账户和密码
        if(userAccount.length() < 4 || userPassword.length() < 4) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
            // 3. 密码和确认密码是否相等
        if(!userPassword.equals(checkPassword)) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
            // 4. 判断是否有特殊字符  账户是否包含
        String regx = "[ _`!@#$%^&*()+=|{}’:;’,.<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(regx).matcher(userAccount);
        if(matcher.find()) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "账户包含特殊字符");

        // 02. 判断账户是否重复
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "账户重复");

        // 03. 密码加密
        String newPassword = DigestUtils.md5DigestAsHex((SLAT + userPassword).getBytes());

        // 03. 注入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        boolean is_save = this.save(user);
        if (is_save) {
            return user.getId();
        }

        // 04. 以上都没有成功返回的结果
        throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "注册失败");


    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 01. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);;
        if(userAccount.length() < 4 || userPassword.length() < 4) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);;

        // 02. 判断是否有特殊字符  账户是否包含
        String regx = "[ _`!@#$%^&*()+=|{}’:;’,.<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(regx).matcher(userAccount);
        if(matcher.find()) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "账户名含特殊字符");

        // 03. 密码加密
        String newPassword = DigestUtils.md5DigestAsHex((SLAT + userPassword).getBytes());

        // 04. 查询账户是否存在
        QueryWrapper queryWrapper = new QueryWrapper<>();
            // 这里需要添加逻辑删除注解  如果已经被删除了就不应该被查询出来
        queryWrapper.eq("userAccount", userAccount);
            // 这里的密码应该时加密之后的密码   否则就会匹配不到
        queryWrapper.eq("userPassword", newPassword);
        User user = userMapper.selectOne(queryWrapper);

        if(user == null) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS, "账户名不存在");

        // 05. 记录用户的登录态
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        // 06. 返回用户脱敏之后的用户信息
        return safetyUser;
    }


    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        // 01. 判空   使用集合工具类
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 进行查询
        // 使用sql方式进行查询
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        // 使用for循环及性能遍历里面所有的json字符串   tags存储标签的格式为["java", "python", "c++"...]
//        for (String tagName : tagNameList) {
//            // querywrapper使用like可以进行 模糊查询  循环遍历就会一直往后面追加模糊查询
//            queryWrapper = queryWrapper.like("tags", tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
        // 使用内存的方式进行查询
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            // 先将所有的用户信息都查询出来
        List<User> userList1 = userMapper.selectList(queryWrapper1);
        Gson gson = new Gson();
            // 在内存中判断符合tag标签的用户进行返回
        return userList1.stream().filter(user -> {
            // 获取到每个用户的tag标签
            String tagStr = user.getTags();
            // 有些是没有tags数据的如果不判空就会返回空指针异常
//            if(StringUtils.isBlank(tagStr)) {
//                return false;
//            }
            // 将其转变成集合类型的java对象 使用json序列化
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>(){}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if(tempTagNameSet.contains(tagName)) {
                    return true;
                }
            }
            return false;
        }).map(user -> getSafetyUser(user)).collect(Collectors.toList());
    }


    /**
     * 修改用户信息
     * @param user  前端传递过来需要修改的用户信息   userAccount:"xxx"
     * @param loginUser  登录之后的用户信息
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        int id = user.getId();
        if(id <= 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 01. 管理员可以修改自己或者其他人
//        if(isAdmin(loginUser)) {
//            User oldUser = userMapper.selectById(id);
//            if(oldUser == null) {
//                throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
//            }
//            // 更新成功之后会返回影响的数据库条数
//            return userMapper.updateById(user);
//
//        }
//        // 02. 普通用户只能修改自己  判断是否是自己通过id进行判断  登录的用户信息里面的id和传递过来的user里面的id
//        if(id != loginUser.getId()) {
//            throw new BusinessResponseException(ErrorCode.NO_AUTHOR);
//        }
//        User oldUser = userMapper.selectById(id);
//        if(oldUser == null) {
//            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
//        }
//        // 更新成功之后会返回影响的数据库条数
//        return userMapper.updateById(user);


        // 代码合并   && 必须同时成立  只要有一个不成立就会执行后面的代码
        if(!isAdmin(loginUser) && id != loginUser.getId()) {
            throw new BusinessResponseException(ErrorCode.NO_AUTHOR);
        }
        User oldUser = userMapper.selectById(id);
        if(oldUser == null) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 更新成功之后会返回影响的数据库条数
        return userMapper.updateById(user);

    }


    // 方法1：  用户信息脱敏
    public User getSafetyUser(User user) {
        User safty_user = new User();
        safty_user.setId(user.getId());
        safty_user.setUsername(user.getUsername());
        safty_user.setUserAccount(user.getUserAccount());
        safty_user.setGender(user.getGender());
        safty_user.setPhone(user.getPhone());
        safty_user.setEmail(user.getEmail());
        safty_user.setPlantCode(user.getPlantCode());
        safty_user.setUserRole(user.getUserRole());
        safty_user.setUserStatus(user.getUserStatus());
        safty_user.setIsDelete(user.getIsDelete());
        safty_user.setAvatarUrl(user.getAvatarUrl());
        safty_user.setUserAddress(user.getUserAddress());
        safty_user.setTags(user.getTags());
        safty_user.setProfile(user.getProfile());
        return safty_user;
    }




    // 方法2：判断是否为管理员
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 01. 获取到用户登录态
        User userObj = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        int userRole = userObj.getUserRole();
        if (userRole != 1 || userObj == null) return false;
        return true;
    }
    // 方法重载  参数不一样
    @Override
    public boolean isAdmin(User user) {
        // 01. 获取到用户登录态
        int userRole = user.getUserRole();
        if (userRole != 1 || user == null) return false;
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 01. 判断是否为空
        if(request == null) {
            return null;
        }
        // 02. 获取user对象
        Object loginUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(loginUser == null) {
            throw new BusinessResponseException(ErrorCode.NO_LOGIN);
        }
        return (User) loginUser;
    }


    /**
     * 用户推荐接口
     * @param pageNum  页码
     * @param pageSize  每页条数
     * @param request  请求
     * @return
     */
    @Override
    public Page<User> recommend(long pageNum, long pageSize, HttpServletRequest request) {
        // 01. 获取用户的登录信息
        User loginUser = getLoginUser(request);
        Integer userId = loginUser.getId();
        // 02. 设置一个key模板  redis存储时使用
        String keyStrRedis = String.format("way:user:recommend:%s", userId);
        // 03. 创建一个redis的操作集合
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 04. 判断是否存在缓存  有的话就直接读取缓存   相反就创建缓存
            // 使用Page对象 方便进行分页查询
        Page<User> userPage = (Page<User>)valueOperations.get(keyStrRedis);
        if(userPage != null) {
            return userPage;
        }
        // 05. 没有缓存的情况
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<User>(pageNum, pageSize), queryWrapper);
        try {
            valueOperations.set(keyStrRedis, userPage, 40000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("valueOperations set is fail");
        }
        return userPage;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        final int userId = loginUser.getId();
        // 01. 创建一个查询对象 只查询id 和 tags 并且过滤掉空的数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> list = this.list(queryWrapper);
        // 02. 获取到当前登录用户的tags  它是作为编辑距离算法的根本  将所有的用户的tags和当前登录用户的tag进行比较找出最相似的几个用户即可 （原理）
        String tags = loginUser.getTags();
        // 03. 使用gson将json格式序列化为一个列表形式
        Gson gson = new Gson();
        List<String> loginUserTagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 04. 使用pair来临时存储用户信息 和相似度分数
        List<Pair<User, Long>> pairList = new ArrayList<>();
        // 05. for循环依次遍历所有用户信息来计算相似度
        for (int i = 0; i < list.size(); i++) {
            // 05-1. 获取user对象的tags
            User user = list.get(i);
            String tagsP = user.getTags();
            // 05-2. 不要标签为空的用户 和 自己（它会查到自己）
            if(StringUtils.isBlank(tagsP) || user.getId() == userId) {
                continue;    // 表示跳过当前循环继续执行  break表示终止循环
            }
            // 05-3. 将当前的tag进行序列化
            List<String> tagPList = gson.fromJson(tagsP, new TypeToken<List<String>>() {}.getType());
            // 05-4. 调用编辑距离算法进行计算相似度  然后追加到pair里面存储  存储用户信息和分数
            long score = AlgoUtils.minDistance(loginUserTagList, tagPList);
            pairList.add(new Pair<>(user, score));
        }
        // 06. 按照编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = pairList.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 07. 将pair里面的id单独提取出来  目的是为了我们能够获取到所有的用户信息然后再打印出来   使用map能进行一个新的映射
        List<Integer> idList = topUserPairList.stream().map(a -> a.getKey().getId()).collect(Collectors.toList());
        // 08. 创建查询对象，查询当前id在id列表里面的用户数据
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 查询当前id在这个id列表里面的用户数据
        userQueryWrapper.in("id", idList);
        // 09. 将查询出来的结果使用一个map集合进行存储 userid1 -> user1   userid2 -> user2...
        Map<Integer, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        // 10. 创建一个新的集合对象来将顺序进行规整   需要返回的顺序 1 2 3   前面map的顺序 1 3 2
        List<User> finalUserList = new ArrayList<>();
        // 使用for循环遍历前面的正确顺序的idList  然后将map里面的数据调用get方法，传递id且获取一个  最后追加到list里面返回即可
        for (Integer integerUserId : idList) {
            finalUserList.add(userIdUserListMap.get(integerUserId).get(0));
        }
        return finalUserList;
    }
}




