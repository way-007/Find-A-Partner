package com.way.threes_company_backend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest {



    /**
     * 每页的数据量
     */
    protected int pageSize = 10;


    /**
     * 几页
     */
    protected int pageNum = 1;
}
