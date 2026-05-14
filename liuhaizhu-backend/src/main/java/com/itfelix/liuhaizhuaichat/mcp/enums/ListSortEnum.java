package com.itfelix.liuhaizhuaichat.mcp.enums;

/**
 * 列表排序枚举
 * @author aceFelix
 */
public enum ListSortEnum {
    ASC("asc","升序"),
    DESC("desc","降序");
    public final String type;
    public final String value;
    ListSortEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
