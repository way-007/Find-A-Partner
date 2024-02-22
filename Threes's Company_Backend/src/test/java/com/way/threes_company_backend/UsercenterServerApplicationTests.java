package com.way.threes_company_backend;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.way.threes_company_backend.mapper.UserMapper;
import com.way.threes_company_backend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UsercenterServerApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(userList.size() > 0, "");
        userList.forEach(System.out::println);
    }

}
