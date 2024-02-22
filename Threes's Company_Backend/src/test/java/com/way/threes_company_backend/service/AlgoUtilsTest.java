package com.way.threes_company_backend.service;


import com.way.threes_company_backend.utils.AlgoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AlgoUtilsTest {

    @Test
    void test1() {
        String str1 = "你是狗";
        String str2 = "你是狗吗";
        String str3 = "我是你打野";
//        System.out.println(AlgoUtils.minDistance(str1, str2));
//        System.out.println(AlgoUtils.minDistance(str1, str3));
    }

    @Test
    void test2() {
        List<String> list1 = Arrays.asList("java", "大二");
        List<String> list2 = Arrays.asList("java", "大三");
        List<String> list3 = Arrays.asList("java", "大四", "重庆", "男");
        System.out.println(AlgoUtils.minDistance(list1, list2));
        System.out.println(AlgoUtils.minDistance(list1, list3));
    }
}
