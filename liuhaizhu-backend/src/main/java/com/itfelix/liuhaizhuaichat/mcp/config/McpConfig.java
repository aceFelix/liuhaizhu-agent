package com.itfelix.liuhaizhuaichat.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import org.springframework.ai.mcp.client.autoconfigure.NamedClientMcpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * MCP服务配置
 * @author aceFelix
 */
/*@Configuration
public class McpConfig {
    @Bean
    public List<NamedClientMcpTransport> mcpClientTransport() {
        McpClientTransport transport = HttpClientSseClientTransport
                .builder("https://mcp.amap.com")
                .sseEndpoint("/sse?key=99e096fb1e17eb5be1af128f95060711")
                .objectMapper(new ObjectMapper())
                .build();
        return Collections.singletonList(new NamedClientMcpTransport("amap", transport));
    }
}*/
