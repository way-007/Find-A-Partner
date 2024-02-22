package com.way.threes_company_backend.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.way.threes_company_backend.common.PageRequest;
import com.way.threes_company_backend.model.domain.UserTeam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 请求参数包装类：
 *  1. 请求参数名称和实体类不一样
 *  2. 有一些参数用不到，如果生成接口文档会增加理解成本
 *  3. 将多个字段映射到同一个类
 */
//@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
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
     * 我加入的队伍id  关系表查出的
     */
    private List<Integer> idList;
    /**
     * 搜索关键词  同时对队伍名称和描述进行搜索
     */
    private String searchKey;


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

}









