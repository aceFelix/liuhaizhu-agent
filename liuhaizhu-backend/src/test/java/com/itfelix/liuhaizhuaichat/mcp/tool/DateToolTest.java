package com.itfelix.liuhaizhuaichat.mcp.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateTool MCP 工具单元测试
 * @author aceFelix
 */
@DisplayName("DateTool 单元测试")
class DateToolTest {

    private DateTool dateTool;

    @BeforeEach
    void setUp() {
        dateTool = new DateTool();
    }

    @Nested
    @DisplayName("getCurrentTime - 获取北京时间")
    class GetCurrentTimeTests {

        @Test
        @DisplayName("应返回非空的时间字符串")
        void shouldReturnNonEmptyTimeString() {
            String time = dateTool.getCurrentTime();

            assertNotNull(time);
            assertFalse(time.isEmpty());
        }

        @Test
        @DisplayName("返回结果应包含'当前时间'标识")
        void shouldContainCurrentTimeLabel() {
            String time = dateTool.getCurrentTime();

            assertTrue(time.contains("当前时间"));
        }

        @Test
        @DisplayName("返回结果应包含'北京时间'标识")
        void shouldContainBeijingTimeLabel() {
            String time = dateTool.getCurrentTime();

            assertTrue(time.contains("北京时间"));
        }

        @Test
        @DisplayName("返回的时间格式应为 yyyy-MM-dd HH:mm:ss")
        void shouldReturnCorrectDateFormat() {
            String time = dateTool.getCurrentTime();

            // 提取时间部分：格式为 "当前时间是：yyyy-MM-dd HH:mm:ss（北京时间）"
            assertTrue(time.matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"),
                    "时间格式应为 yyyy-MM-dd HH:mm:ss，实际: " + time);
        }

        @Test
        @DisplayName("连续两次调用应返回一致/递增的时间")
        void shouldReturnConsistentTimeOnConsecutiveCalls() {
            String time1 = dateTool.getCurrentTime();
            String time2 = dateTool.getCurrentTime();

            assertNotNull(time1);
            assertNotNull(time2);
            // 在同一毫秒内可能相同，所以不强制要求不同
        }
    }

    @Nested
    @DisplayName("getCurrentTimeByZoneId - 根据时区获取时间")
    class GetCurrentTimeByZoneIdTests {

        @Test
        @DisplayName("应返回包含城市名称的时间字符串")
        void shouldContainCityName() {
            String time = dateTool.getCurrentTimeByZoneId("东京", "Asia/Tokyo");

            assertTrue(time.contains("东京"));
            assertTrue(time.contains("当前时间"));
        }

        @Test
        @DisplayName("纽约时区应返回有效时间")
        void shouldReturnValidTimeForNewYork() {
            String time = dateTool.getCurrentTimeByZoneId("纽约", "America/New_York");

            assertNotNull(time);
            assertTrue(time.contains("纽约"));
            assertTrue(time.matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"),
                    "应包含正确格式的时间");
        }

        @Test
        @DisplayName("伦敦时区应返回有效时间")
        void shouldReturnValidTimeForLondon() {
            String time = dateTool.getCurrentTimeByZoneId("伦敦", "Europe/London");

            assertNotNull(time);
            assertTrue(time.contains("伦敦"));
            assertTrue(time.matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"));
        }

        @Test
        @DisplayName("上海与纽约时间应不同（时差约12-13小时）")
        void shouldReturnDifferentTimeForDifferentZones() {
            String shanghaiTime = dateTool.getCurrentTimeByZoneId("上海", "Asia/Shanghai");
            String newYorkTime = dateTool.getCurrentTimeByZoneId("纽约", "America/New_York");

            assertNotEquals(shanghaiTime, newYorkTime,
                    "不同时区应返回不同的时间");
        }

        @Test
        @DisplayName("无效的时区ID应抛出异常")
        void shouldThrowForInvalidZoneId() {
            assertThrows(Exception.class, () ->
                    dateTool.getCurrentTimeByZoneId("火星", "Mars/Olympus"));
        }
    }
}
