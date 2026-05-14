package com.itfelix.liuhaizhuaichat.mcp.enums;

/**
 * 商品状态枚举
 * @author aceFelix
 */
public enum ProductStatusEnum {
    ON_SALE(1,"上架"),
    OFF_SALE(0,"下架"),
    PRE_SALE(2,"预售");
    public final int type;
    public final String value;
    ProductStatusEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
