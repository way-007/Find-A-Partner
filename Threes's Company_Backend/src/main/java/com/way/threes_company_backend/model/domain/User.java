package com.way.threes_company_backend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "username")
    private String username;

    /**
     * 账户
     */
    @TableField(value = "userAccount")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "userPassword")
    private String userPassword;

    /**
     * 性别
0 - 女
1 - 男
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 
     */
    @TableField(value = "email")
    private String email;

    /**
     * 
     */
    @TableField(value = "plantCode")
    private Integer plantCode;

    /**
     * 
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 身份
0 - 普通用户
1 - 管理员
     */
    @TableField(value = "userRole")
    private Integer userRole;

    /**
     * 是否有效
0 - 无效
1 - 有效
     */
    @TableField(value = "userStatus")
    private Integer userStatus;

    /**
     * 是否逻辑删除
1 - 否
0 - 是
     */
//    @TableLogic
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 
     */
    @TableField(value = "avatarUrl")
    private String avatarUrl;

    /**
     *  标签  json
     */
    @TableField(value = "tags")
    private String tags;

    /**
     *  自我介绍  profile
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 
     */
    @TableField(value = "userAddress")
    private String userAddress;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}