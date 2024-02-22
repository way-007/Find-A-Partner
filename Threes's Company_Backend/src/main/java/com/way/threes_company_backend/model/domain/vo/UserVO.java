package com.way.threes_company_backend.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;



@Data
public class UserVO {
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
