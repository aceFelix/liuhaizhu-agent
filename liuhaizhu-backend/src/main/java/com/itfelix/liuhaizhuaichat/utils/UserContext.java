package com.itfelix.liuhaizhuaichat.utils;

import com.itfelix.liuhaizhuaichat.pojo.entity.User;

/**
 * 用户上下文工具类
 * 用于在异步线程中传递当前登录用户信息
 * @author aceFelix
 */
public class UserContext {

    // 使用 InheritableThreadLocal 使子线程可以继承父线程的值
    private static final InheritableThreadLocal<User> CURRENT_USER = new InheritableThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(User user) {
        CURRENT_USER.set(user);
    }

    /**
     * 获取当前用户
     */
    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        CURRENT_USER.remove();
    }
}
