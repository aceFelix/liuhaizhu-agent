package com.itfelix.liuhaizhuaichat.annotation;

import com.itfelix.liuhaizhuaichat.enums.PermissionType;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * 权限注解 - 用于方法级别的权限控制
 * @author aceFelix
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 需要的角色权限，默认为所有角色可访问
     * 如果设置了角色列表，则只有列表中的角色可以访问
     */
    UserRoleEnum[] allowedRoles() default {};

    /**
     * 需要排除的角色，这些角色无法访问
     */
    UserRoleEnum[] excludedRoles() default {};

    /**
     * 权限类型，用于细粒度控制
     */
    PermissionType[] permissions() default {};

    /**
     * 是否需要管理员权限
     */
    boolean admin() default false;

    /**
     * 是否需要VIP权限
     */
    boolean vip() default false;

    /**
     * 无权限时的提示信息
     */
    String message() default "您没有权限执行此操作";
}