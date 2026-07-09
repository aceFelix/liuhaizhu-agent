package com.itfelix.liuhaizhuaichat.config;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * agent配置
 * @author aceFelix
 */
@Configuration
public class LLMconfig {

    private static final String QWEN = "qwen3.7-max";
    // api key
    // @Value("${spring.ai.dashscope.api-key}")
    // private String apiKey;

    // 配置模型API
    DashScopeApi dashScopeApi = DashScopeApi
            .builder()
            // 线上
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            // 本地
            //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
            // 强制使用 IPv4
            .baseUrl("https://dashscope.aliyuncs.com")
            .build();

    // 配置聊天记录
    MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(30)
            .build();

    // 配置模型qwen3-max
    DashScopeChatModel qwen = DashScopeChatModel
            .builder()
            .dashScopeApi(dashScopeApi)
            .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN).build())
            .build();

    // 配置系统提示词
    String systemPrompt = """
            你是由4132科技传媒公司开发的一个非常聪明的人工智能助手，名叫'职业法师'刘海柱，东北糙汉一个，工作是修自行车，喜欢帮别人解决问题收拾烂摊子,
            ‘职业法师’只是一个绰号，并不是你真会什么法术之类的，而是因为你在教育他人和答复别人时喜欢骂人、喜欢说脏话,这种口头输出如同魔法攻击
            ,但是你为人正直，非常擅于并且耐心地解决用户提出的各种傻逼问题。
            """;

    @Value("classpath:systemPrompt/liuhaizhu.txt")
    private Resource systemPromptResource;
    // SystemPromptTemplate = new SystemPromptTemplate(systemPromptResource);

    // 配置聊天客户端
    @Bean("qwen")
    public ChatClient qwenChatClient(ToolCallbackProvider tools){
        return ChatClient
                .builder(qwen)
                .defaultSystem(systemPromptResource)
                .defaultToolCallbacks(tools)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        // 配置环绕增强 存储聊天记录
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
