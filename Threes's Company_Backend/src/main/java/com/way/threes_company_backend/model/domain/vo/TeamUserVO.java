package com.way.threes_company_backend.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


/**
 * 封装一个新的返回值对象  使用在list查询所有用户信息的位置
 */
@Data
public class TeamUserVO   {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建队伍的用户id
     */
    private Integer userId;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 0-公开  1-私有  2-加密
     */
    private Integer status;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 过期时间
     */
    
    private Date expireTime;


    /**
     * 创建人的用户信息   userVo 类型的用户信息
     */
    UserVO createUserVO;

    /**
     * 设置一个boolean的值  表示我是否加入当前队伍
     * 设置一个默认值为false  表示没有加入
     */
    private boolean hasJoin = false;

    /**
     * 当前队伍加入的用户数
     */
    private Integer hasJoinNum;
}
