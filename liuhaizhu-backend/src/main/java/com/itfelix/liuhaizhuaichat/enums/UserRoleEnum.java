package com.itfelix.liuhaizhuaichat.enums;

import lombok.Getter;

/**
 * @author aceFilex
 */
@Getter
public enum UserRoleEnum {
    ADMIN("ADMIN", "管理员"),
    VIP("VIP", "VIP用户"),
    USER("USER", "普通用户");

    private final String code;
    private final String desc;

    UserRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
