package com.itfelix.liuhaizhuaichat.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * @author aceFelix
 */
public class MyColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        return switch (level.toInt()) {
            // 错误级别为红色
            case Level.ERROR_INT -> ANSIConstants.RED_FG;
            // 警告级别为黄色
            case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
            // 信息级别为绿色
            case Level.INFO_INT -> ANSIConstants.GREEN_FG;
            // 调试级别为青色
            case Level.DEBUG_INT -> ANSIConstants.CYAN_FG;
            // 默认颜色
            default -> ANSIConstants.DEFAULT_FG;
        };
    }
}
