package com.way.threes_company_backend.model.domain.request;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class JoinParams {
    // 01. 加入的队伍id
    private Integer id;

    // 02. 加入的密码
    private String password;
}
