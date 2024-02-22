package com.way.threes_company_backend.model.domain.request;

import lombok.Data;

@Data
public class UserLoginParams {

    private String userAccount;

    private String userPassword;
}
