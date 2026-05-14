package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import com.itfelix.liuhaizhuaichat.service.ChatService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author aceFelix
 */
@RestController
@RequestMapping("api/webSearch")
public class WebSearchController {
    @Autowired
    private ChatService chatService;

    /**
     * 启用联网搜索
     * @param chatDTO
     */
    @PostMapping("query")
    public void webSearch(@RequestBody ChatDTO chatDTO, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        chatService.doChatWebSearch(chatDTO);
    }
}
