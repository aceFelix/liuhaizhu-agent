package com.itfelix.liuhaizhuaichat.mcp.enums;

/**
 * 价格比较枚举
 * @author aceFelix
 */
public enum PriceCompareEnum {
    LOWER("<","价格低于"),
    EQUAL("=","价格等于"),
    HIGHER(">","价格高于");
    public final String type;
    public final String value;
    PriceCompareEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
