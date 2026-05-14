package com.itfelix.liuhaizhuaichat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.TokenUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface TokenUsageMapper extends BaseMapper<TokenUsage> {

    /**
     * 查询指定日期的用户Token消耗
     */
    TokenUsage selectByUserIdAndDate(@Param("userId") String userId, @Param("usageDate") LocalDate usageDate);

    /**
     * 按用户汇总Token消耗（全部历史）
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT cm.user_id AS userId, u.username AS username, u.email AS email, " +
            "SUM(cm.token_count) AS totalTokens, COUNT(DISTINCT DATE(cm.create_time)) AS activeDays " +
            "FROM chat_message cm " +
            "LEFT JOIN user u ON cm.user_id = u.user_id " +
            "WHERE cm.role = 'assistant' AND cm.token_count > 0 " +
            "GROUP BY cm.user_id, u.username, u.email " +
            "ORDER BY totalTokens DESC")
    List<Map<String, Object>> selectUserTokenSummary();

    /**
     * 按用户+日期查询Token消耗明细
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT cm.user_id AS userId, u.username AS username, u.email AS email, " +
            "DATE(cm.create_time) AS usageDate, SUM(cm.token_count) AS tokenCount " +
            "FROM chat_message cm " +
            "LEFT JOIN user u ON cm.user_id = u.user_id " +
            "WHERE cm.user_id = #{userId} AND cm.role = 'assistant' AND cm.token_count > 0 " +
            "GROUP BY cm.user_id, u.username, u.email, DATE(cm.create_time) " +
            "ORDER BY usageDate DESC")
    List<Map<String, Object>> selectUserTokenDetail(@Param("userId") String userId);

    /**
     * 查询系统总Token消耗
     */
    @org.apache.ibatis.annotations.Select("SELECT COALESCE(SUM(token_count), 0) FROM chat_message WHERE role = 'assistant' AND token_count > 0")
    Long selectTotalTokens();

    /**
     * 按小时汇总Token消耗（系统级，从chat_message表查询）
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT DATE_FORMAT(create_time, '%Y-%m-%d %H:00') AS timeSlot, " +
            "COALESCE(SUM(token_count), 0) AS tokenCount " +
            "FROM chat_message " +
            "WHERE role = 'assistant' AND token_count > 0 " +
            "GROUP BY timeSlot " +
            "ORDER BY timeSlot DESC " +
            "LIMIT 168")
    List<Map<String, Object>> selectHourlySummary();

    /**
     * 按小时汇总指定用户的Token消耗
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT DATE_FORMAT(create_time, '%Y-%m-%d %H:00') AS timeSlot, " +
            "COALESCE(SUM(token_count), 0) AS tokenCount " +
            "FROM chat_message " +
            "WHERE user_id = #{userId} AND role = 'assistant' AND token_count > 0 " +
            "GROUP BY timeSlot " +
            "ORDER BY timeSlot DESC " +
            "LIMIT 168")
    List<Map<String, Object>> selectHourlyDetail(@Param("userId") String userId);

    /**
     * 按周汇总Token消耗（系统级）
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT CONCAT(YEAR(usage_date), '-W', LPAD(WEEK(usage_date, 1), 2, '0')) AS timeSlot, " +
            "COALESCE(SUM(token_count), 0) AS tokenCount " +
            "FROM token_usage " +
            "GROUP BY timeSlot " +
            "ORDER BY timeSlot DESC " +
            "LIMIT 52")
    List<Map<String, Object>> selectWeeklySummary();

    /**
     * 按周汇总指定用户的Token消耗
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT CONCAT(YEAR(usage_date), '-W', LPAD(WEEK(usage_date, 1), 2, '0')) AS timeSlot, " +
            "COALESCE(SUM(token_count), 0) AS tokenCount " +
            "FROM token_usage " +
            "WHERE user_id = #{userId} " +
            "GROUP BY timeSlot " +
            "ORDER BY timeSlot DESC " +
            "LIMIT 52")
    List<Map<String, Object>> selectWeeklyDetail(@Param("userId") String userId);

    /**
     * 按天汇总Token消耗（系统级）
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT usage_date AS timeSlot, token_count AS tokenCount " +
            "FROM token_usage " +
            "ORDER BY usage_date DESC " +
            "LIMIT 90")
    List<Map<String, Object>> selectDailySummary();
}
