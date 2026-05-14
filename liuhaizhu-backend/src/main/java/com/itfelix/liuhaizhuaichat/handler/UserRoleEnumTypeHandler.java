package com.itfelix.liuhaizhuaichat.handler;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * 用户角色枚举类型处理器
 * 用于在数据库中存储和检索用户角色枚举值
 * @author aceFelix
 */
@Component
@MappedTypes(UserRoleEnum.class)
public class UserRoleEnumTypeHandler extends BaseTypeHandler<UserRoleEnum> {

    /**
     * 将用户角色枚举值设置到数据库中
     * @param ps       预编译语句
     * @param i        参数索引
     * @param parameter 用户角色枚举值
     * @param jdbcType JDBC类型
     * @throws SQLException 如果设置参数时发生SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRoleEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }
    /**
     * 从数据库中检索用户角色枚举值
     * @param rs        结果集
     * @param columnName 列名
     * @return 用户角色枚举值
     * @throws SQLException 如果检索结果时发生SQL异常
     */
    @Override
    public UserRoleEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : getEnumByCode(code);
    }
    /**
     * 从数据库中检索用户角色枚举值
     * @param rs        结果集
     * @param columnIndex 列索引
     * @return 用户角色枚举值
     * @throws SQLException 如果检索结果时发生SQL异常
     */
    @Override
    public UserRoleEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : getEnumByCode(code);
    }
    /**
     * 从数据库中检索用户角色枚举值
     * @param cs        调用语句
     * @param columnIndex 列索引
     * @return 用户角色枚举值
     * @throws SQLException 如果检索结果时发生SQL异常
     */
    @Override
    public UserRoleEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : getEnumByCode(code);
    }
    /**
     * 根据角色编码获取用户角色枚举值
     * @param code 角色编码
     * @return 用户角色枚举值
     */
    private UserRoleEnum getEnumByCode(String code) {
        for (UserRoleEnum roleEnum : UserRoleEnum.values()) {
            if (roleEnum.getCode().equals(code)) {
                return roleEnum;
            }
        }
        return null;
    }
}
