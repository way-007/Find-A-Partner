package com.way.threes_company_backend.service;

import com.way.threes_company_backend.mapper.UserMapper;
import com.way.threes_company_backend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testSearchUserByTags() {
        // 使用arrays.aslist直接生成一个集合  里面传入自己想要传入的字符串数据即可  就不是空的
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> users = userService.searchUserByTags(tagNameList);
        Assert.isTrue(users != null);
        // 输出
        for (User user : users) {
            System.out.println(user);
        }
    }




    @Resource
    private UserMapper userMapper;

    /**
     * 方式一：  使用usermapper + for循环进行单条的数据插入
     *
     * 缺点：  1. 花费时间在数据库的连接和释放上面         2. for循环是绝对的线性的  必须等到前一次请求成功之后才能再次进行
     * Creating a new SqlSession
     * SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5d18a787] was not registered for synchronization because synchronization is not active
     * JDBC Connection [HikariProxyConnection@1011399667 wrapping com.mysql.cj.jdbc.ConnectionImpl@711261c7] will not be managed by Spring
     * ==>  Preparing: INSERT INTO user ( username, userAccount, userPassword, gender, phone, email, userRole, userStatus, isDelete, avatarUrl, tags, profile, userAddress ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * ==> Parameters: 小黑子(String), fuck dragon(String), 123456(String), 0(Integer), 15223315641(String), 1231232@qq.com(String), 0(Integer), 0(Integer), 0(Integer), https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png(String), ['java', 'c++', 'python'](String), 大家好我是一只小黑子(String), 重庆(String)
     * <==    Updates: 1
     * Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5d18a787]
     */
    @Test
    public void doInsertUsersMethod1() {
        // 01. 记时
        // 02. 定义需要插入的数据量
        final int INSERT_NUM = 1000;

        // 03. 使用循环的方式创建
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("小黑子");
            user.setUserAccount("fuck dragon");
            user.setUserPassword("123456");
            user.setGender(0);
            user.setPhone("15223315641");
            user.setEmail("1231232@qq.com");
            user.setUserRole(0);
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setAvatarUrl("https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png");
            user.setTags("['java', 'c++', 'python']");
            user.setProfile("大家好我是一只小黑子");
            user.setUserAddress("重庆");

            // 将创建的user对象追加到集合里面进行存储
            userMapper.insert(user);
        }

    }


    /**
     * 方法二： 使用userservice的批量方法 saveBatch（传入一个集合里面存入所有需要插入的数据， size[需要分批插入每次分批的数据量]）
     *
     */
    @Test
    public void doInsertUsersMethod2() {
        // 01. 记时
        // 02. 定义需要插入的数据量
        final int INSERT_NUM = 100000;

        // 03. 创建一个user类型的集合 将所有的user数据存储到集合中
        List<User> userList = new ArrayList<>();
        // 03. 使用循环的方式创建
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("小黑子");
            user.setUserAccount("fuck dragon");
            user.setUserPassword("123456");
            user.setGender(0);
            user.setPhone("15223315641");
            user.setEmail("1231232@qq.com");
            user.setUserRole(0);
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setAvatarUrl("https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png");
            user.setTags("['java', 'c++', 'python']");
            user.setProfile("大家好我是一只小黑子");
            user.setUserAddress("重庆");

            // 将创建的user对象追加到集合里面进行存储
            userList.add(user);
        }
        userService.saveBatch(userList, 1000);  // 10 插入10条
    }


    /**
     * 方法三： 多线程的分批执行  +  saveBatch
     *  10 0000 分成10组 每组1万条
     *   01. 先将所有数据分为10个集合进行存储  for循环里面  然后每个集合存储1万条数据
     *   02. 创建一个线程来进行异步的执行  completable.runasync()
     *   03. 创建一个集合将线程执行的结果都追加到集合中
     *   04. 执行线程    10万条数据只需要花费12s
     */
    @Test
    public void doInsertUsersMethod3() {
        // 01. 记时
        // 02. 定义需要插入的数据量
        final int INSERT_NUM = 1000;
        // 03. 创建一个集合存储线程集合
        List<CompletableFuture<Void>> futuersList = new ArrayList<>();
        // 03. 使用循环的方式将10万条数据分为10组 每组1万条
        int j = 0;
        for (int i = 0; i < 10; i++) {
            // 创建一个数组 分为10个  每个数组保存1万条数据
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("小黑子");
                user.setUserAccount("fuck dragon");
                user.setUserPassword("123456");
                user.setGender(0);
                user.setPhone("15223315641");
                user.setEmail("1231232@qq.com");
                user.setUserRole(0);
                user.setUserStatus(0);
                user.setIsDelete(0);
                user.setAvatarUrl("https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png");
                user.setTags("['java', 'c++', 'python']");
                user.setProfile("大家好我是一只小黑子");
                user.setUserAddress("重庆");
                userList.add(user);
                if(j % 10000 == 0) {
                    break;
                }
            }
            // 创建一个线程来异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName" + Thread.currentThread().getName());
                userService.saveBatch(userList, 10000);  // 每组1万 总共10万 分为10组
            });
            // 将线程添加到集合里面去
            futuersList.add(future);
        }

        // 05. 执行线程
        CompletableFuture.allOf(futuersList.toArray(new CompletableFuture[]{})).join();

    }

}