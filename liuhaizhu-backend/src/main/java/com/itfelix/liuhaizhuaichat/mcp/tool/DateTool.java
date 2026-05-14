package com.itfelix.liuhaizhuaichat.mcp.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间MCP服务
 * 获取当前时间
 * @author aceFelix
 */
@Component
@Slf4j
public class DateTool {

    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(description = "获取当前时间（北京时间）")
    public String getCurrentTime() {
        log.info("===================获取当前时间getCurrentTime===================");
        String currentTime = String.format("当前时间是：%s（北京时间）",
                ZonedDateTime.now(BEIJING_ZONE).format(FORMATTER));
        log.info("当前时间：{}", currentTime);
        return currentTime;
    }

    @Tool(description = "根据城市所在的时区ID来获取当前时间")
    public String getCurrentTimeByZoneId(String cityName, String zoneId) {
        log.info("===================获取当前时间getCurrentTimeByZoneId===================");
        log.info("城市名称：{}", cityName);
        log.info("时区ID：{}", zoneId);
        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
        String currentTime = String.format("%s当前时间是：%s",
                cityName, zonedDateTime.format(FORMATTER));
        log.info("{}当前时间：{}", cityName, currentTime);
        return currentTime;
    }
}
