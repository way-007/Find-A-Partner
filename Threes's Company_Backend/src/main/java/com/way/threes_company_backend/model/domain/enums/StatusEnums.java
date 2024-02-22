package com.way.threes_company_backend.model.domain.enums;

import io.swagger.models.auth.In;

public enum StatusEnums {
    PUBLIC("公开", 0),
    PRIVATE("私有", 1),
    SECRET("加密", 2);

    private String text;

    private int value;

    // 方法： 输入对应的value返回对应的text
    public static StatusEnums getEnumsByValue(Integer value) {
        if(value == null) {
            return  null;
        }
        // 获取所有的value到一个集合中
        StatusEnums[] values = StatusEnums.values();
        // 遍历找到了value相等的就会返回对应的text
        for (StatusEnums statusEnum : values) {
            if(statusEnum.getValue() == value) {
                return statusEnum;
            }
        }
        return null;
    }
    StatusEnums(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
