package com.way.threes_company_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.way.threes_company_backend.common.BaseResponse;
import com.way.threes_company_backend.common.ErrorCode;
import com.way.threes_company_backend.common.ResponseUtils;
import com.way.threes_company_backend.exception.BusinessResponseException;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.model.domain.request.UserLoginParams;
import com.way.threes_company_backend.model.domain.request.UserRegisterParams;
import com.way.threes_company_backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController   // 返回的数据格式为json
@RequestMapping("/user")  // 请求的根路径
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
public class userController {

    // 用户登录态
    private final String USER_LOGIN_STATE = "userLoginState";


    @Resource
    private UserService userService;


    // 注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterParams userRegisterParams) {
        // 01. 通过get方法获取到传递过来的所有参数的值
        String userPassword = userRegisterParams.getUserPassword();
        String userAccount = userRegisterParams.getUserAccount();
        String checkPassword = userRegisterParams.getCheckPassword();
        // 02. 使用工具类stringutils判断是否有参数为空的情况
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS);
        // 03. 调用接口的注册方法传递参数
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        // 04. 将结果返回给前端  返回用户注册成功的id
//        return new BaseResponse<>("成功", 0, result);
        return ResponseUtils.success(result);
    }


    // 登录
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginParams userLoginParams, HttpServletRequest request) {
        // 01. 通过get方法获取到传递过来的所有参数的值
        String userPassword = userLoginParams.getUserPassword();
        String userAccount = userLoginParams.getUserAccount();
        // 02. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)) throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS);
        User user = userService.doLogin(userAccount, userPassword, request);
//        return user;
        return ResponseUtils.success(user);
    }

    // 获取当前用户信息
    @GetMapping("/current")
    public BaseResponse<User> userCurrentInfo(HttpServletRequest request) {
        User userObj = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj == null) throw new BusinessResponseException(ErrorCode.NO_LOGIN);
        Integer id = userObj.getId();
        User result = userService.getById(id);
//        return userService.getSafetyUser(result);
        return ResponseUtils.success(result);
    }


    // 退出登录
    @PostMapping("/loginout")
    public BaseResponse<Integer> userLoginOut(HttpServletRequest request) {
        if(request == null) throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        request.getSession().removeAttribute(USER_LOGIN_STATE);
//        return 1;
        return ResponseUtils.success(1);
    }

    // 获取所有的用户信息
    @GetMapping("/search")
    public BaseResponse<List<User>> userSearch(String userAccount, HttpServletRequest request) {
       // 01. 需要传入参数username  和  request
        // 02. 调用方法判断用户名是否为管理员
        if(!userService.isAdmin(request)) throw new BusinessResponseException(ErrorCode.NO_AUTHOR);
        // 03. 使用userService.list方法的模糊查询返回一个列表数组  根据userAccount
        QueryWrapper queryWrapper = new QueryWrapper<>();
        // 04. 调用map.collect方法对齐进行用户脱敏
        if(StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        List<User> list = userService.list(queryWrapper);
//        return list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        List<User> result = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResponseUtils.success(result);
    }


    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList ){
        // 01. 判断是否为空
        if(CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS);
        }
        // 02. 调用方法
        List<User> result = userService.searchUserByTags(tagNameList);
        return ResponseUtils.success(result);
    }


    /**
     * 修改用户信息
     * @param request
     * @param user   登录之后的用户信息
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 01. 判空
        if(user == null) {
            throw new BusinessResponseException(ErrorCode.NO_REQUEST_PARAMS);
        }
        // 02. 判断当前用户是否登录
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null) {
            throw new BusinessResponseException(ErrorCode.NO_LOGIN);
        }
        // 03. 将当前的user 和 登录的request用户信息传递过去
        int result = userService.updateUser(user, loginUser);
        return ResponseUtils.success(result);
    }


    // 获取首页推荐的用户信息
    @GetMapping("/recommend")
    public BaseResponse<IPage<User>> recommendUsers(long pageNum, long pageSize, HttpServletRequest request) {
        Page<User> result = userService.recommend(pageNum, pageSize, request);
        return ResponseUtils.success(result);
    }

    /**
     * 根据标签匹配用户信息
     * @param num  最多匹配的人数
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num,HttpServletRequest request) {
        // 01. 判空
        if(num <= 0) {
            throw new BusinessResponseException(ErrorCode.NO_OK_PARAMS);
        }
        // 02. 获取用户信息
        User loginUser = userService.getLoginUser(request);
        // 03. 调用接口方法
        List<User> result = userService.matchUsers(num, loginUser);
        return ResponseUtils.success(result);
    }

}
