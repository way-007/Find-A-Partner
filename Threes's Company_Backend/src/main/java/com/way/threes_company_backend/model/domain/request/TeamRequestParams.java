package com.way.threes_company_backend.model.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TeamRequestParams {

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


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
    private String password;

    /**
     * 过期时间
     */
    private Date expireTime;
}
