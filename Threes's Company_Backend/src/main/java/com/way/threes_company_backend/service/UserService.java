package com.way.threes_company_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.way.threes_company_backend.model.domain.User;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author way
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-17 21:36:14
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册接口
     * 接口里面调用方法默认就是public不需要手动添加public
     * @param userAccount  账户
     * @param userPassword  密码
     * @param checkPassword  确认密码
     * @return 返回用户的id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    User getSafetyUser(User user);

    /**
     * 用户登录接口
     * @param userAccount  用户名
     * @param userPassword  密码
     * @param request  请求
     * @return
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 根据标签查询用户
     * @param tagNameList
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 修改用户信息
     * @param user
     * @param loginUser
     */
    int updateUser(User user, User loginUser);


    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断是否为管理员  传递的参数是user
     * @param user
     * @return
     */
    // 方法重载  参数不一样
    boolean isAdmin(User user);

    User getLoginUser(HttpServletRequest request);


    /**
     * 用户推荐接口
     * @param pageNum  页码
     * @param pageSize  返回数据量
     * @param request  用户登录信息
     * @return
     */
    Page<User> recommend(long pageNum, long pageSize, HttpServletRequest request);

    /**
     * 根据传递的num来匹配相似度高的用户数量 （匹配是根据tag进行匹配的）
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
