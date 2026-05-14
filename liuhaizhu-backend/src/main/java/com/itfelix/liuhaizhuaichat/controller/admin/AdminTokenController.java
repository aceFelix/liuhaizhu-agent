package com.itfelix.liuhaizhuaichat.controller.admin;

import com.itfelix.liuhaizhuaichat.mapper.TokenUsageMapper;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTokenController {

    private final TokenUsageMapper tokenUsageMapper;

    @GetMapping("/tokens/summary")
    public AceResult<Map<String, Object>> getTokenSummary() {
        List<Map<String, Object>> userTokens = tokenUsageMapper.selectUserTokenSummary();
        Long totalTokens = tokenUsageMapper.selectTotalTokens();

        Map<String, Object> result = new HashMap<>();
        result.put("totalTokens", totalTokens);
        result.put("userTokens", userTokens);

        return AceResult.success(result);
    }

    @GetMapping("/tokens/detail/{userId}")
    public AceResult<List<Map<String, Object>>> getUserTokenDetail(@PathVariable String userId) {
        List<Map<String, Object>> details = tokenUsageMapper.selectUserTokenDetail(userId);
        return AceResult.success(details);
    }

    @GetMapping("/tokens/daily-summary")
    public AceResult<List<Map<String, Object>>> getDailySummary() {
        return AceResult.success(tokenUsageMapper.selectDailySummary());
    }

    @GetMapping("/tokens/hourly-summary")
    public AceResult<List<Map<String, Object>>> getHourlySummary() {
        return AceResult.success(tokenUsageMapper.selectHourlySummary());
    }

    @GetMapping("/tokens/weekly-summary")
    public AceResult<List<Map<String, Object>>> getWeeklySummary() {
        return AceResult.success(tokenUsageMapper.selectWeeklySummary());
    }

    @GetMapping("/tokens/hourly-detail/{userId}")
    public AceResult<List<Map<String, Object>>> getUserHourlyDetail(@PathVariable String userId) {
        return AceResult.success(tokenUsageMapper.selectHourlyDetail(userId));
    }

    @GetMapping("/tokens/weekly-detail/{userId}")
    public AceResult<List<Map<String, Object>>> getUserWeeklyDetail(@PathVariable String userId) {
        return AceResult.success(tokenUsageMapper.selectWeeklyDetail(userId));
    }
}
