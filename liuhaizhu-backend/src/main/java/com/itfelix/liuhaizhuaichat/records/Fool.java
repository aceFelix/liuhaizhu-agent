package com.itfelix.liuhaizhuaichat.records;

/**
 * 傻B脚注
 * @param id
 * @param name
 * Description: jdk14以后的新特性，记录类record：
 * 创建一个类，类中定义了属性，并且属性是final的，并且类中定义了构造函数，并且类中定义了equals和hashCode方法。
 * 这种方式创建等价于entity + lombok
 */
public record Fool(Long id, String name) {
}
