package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.TokenUsageMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;
import com.itfelix.liuhaizhuaichat.pojo.entity.Conversation;
import com.itfelix.liuhaizhuaichat.pojo.vo.ConversationVO;
import com.itfelix.liuhaizhuaichat.service.TitleGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConversationServiceImpl 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConversationServiceImpl 单元测试")
class ConversationServiceImplTest {

    @Mock
    private ConversationMapper conversationMapper;
    @Mock
    private ChatMessageMapper chatMessageMapper;
    @Mock
    private TitleGenerationService titleGenerationService;
    @Mock
    private TokenUsageMapper tokenUsageMapper;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private static final String USER_ID = "user123";
    private static final String CONVERSATION_ID = "conv456";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(conversationService, "maxConversationCount", 50);
    }

    private Conversation createTestConversation(String id, String userId, String title, String type) {
        Conversation conv = new Conversation();
        conv.setId(id);
        conv.setUserId(userId);
        conv.setTitle(title);
        conv.setType(type);
        conv.setPinned(0);
        conv.setCreateTime(LocalDateTime.now());
        conv.setUpdateTime(LocalDateTime.now());
        return conv;
    }

    private ChatMessage createTestMessage(String id, String conversationId, String role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setId(id);
        msg.setConversationId(conversationId);
        msg.setUserId(USER_ID);
        msg.setRole(role);
        msg.setContent(content);
        msg.setMessageId("msg_" + id);
        msg.setCreateTime(LocalDateTime.now());
        return msg;
    }

    @Nested
    @DisplayName("createConversation - 创建会话")
    class CreateConversationTests {

        @Test
        @DisplayName("应成功创建会话并返回会话ID")
        void shouldCreateConversationSuccessfully() {
            when(conversationMapper.selectCount(any())).thenReturn(0L);
            when(conversationMapper.insert(any(Conversation.class))).thenAnswer(inv -> {
                Conversation c = inv.getArgument(0);
                c.setId(CONVERSATION_ID);
                return 1;
            });

            String id = conversationService.createConversation(USER_ID, "新对话", "normal");

            assertNotNull(id);
            assertEquals(CONVERSATION_ID, id);
            verify(conversationMapper).insert(any(Conversation.class));
        }

        @Test
        @DisplayName("超过最大会话数时应清理旧会话")
        void shouldCleanupWhenExceedingMaxCount() {
            // 当前已有 52 个会话（超过上限 50）
            when(conversationMapper.selectCount(any())).thenReturn(52L);

            // 模拟返回 3 个待删除的会话（52 - 50 + 1）
            Conversation old1 = createTestConversation("old1", USER_ID, "旧对话1", "normal");
            Conversation old2 = createTestConversation("old2", USER_ID, "旧对话2", "normal");
            Conversation old3 = createTestConversation("old3", USER_ID, "旧对话3", "normal");
            when(conversationMapper.selectList(any())).thenReturn(Arrays.asList(old1, old2, old3));

            // 模拟消息删除
            when(chatMessageMapper.delete(any())).thenReturn(5);

            // 模拟会话插入
            when(conversationMapper.insert(any(Conversation.class))).thenAnswer(inv -> {
                Conversation c = inv.getArgument(0);
                c.setId(CONVERSATION_ID);
                return 1;
            });

            String id = conversationService.createConversation(USER_ID, "新对话", "normal");

            assertNotNull(id);
            // 验证清理了 3 个旧会话
            verify(conversationMapper, times(3)).deleteById(anyString());
            verify(chatMessageMapper, times(3)).delete(any());
        }
    }

    @Nested
    @DisplayName("listConversations - 获取会话列表")
    class ListConversationsTests {

        @Test
        @DisplayName("应返回用户的会话列表")
        void shouldReturnConversationList() {
            List<ConversationVO> mockList = Arrays.asList(
                    new ConversationVO(), new ConversationVO()
            );
            when(conversationMapper.selectConversationVOList(USER_ID)).thenReturn(mockList);

            List<ConversationVO> result = conversationService.listConversations(USER_ID);

            assertEquals(2, result.size());
            verify(conversationMapper).selectConversationVOList(USER_ID);
        }

        @Test
        @DisplayName("无会话时应返回空列表")
        void shouldReturnEmptyListWhenNoConversations() {
            when(conversationMapper.selectConversationVOList(USER_ID))
                    .thenReturn(Collections.emptyList());

            List<ConversationVO> result = conversationService.listConversations(USER_ID);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getConversationDetail - 获取会话详情")
    class GetConversationDetailTests {

        @Test
        @DisplayName("应返回会话的消息列表")
        void shouldReturnMessagesForValidConversation() {
            Conversation conv = createTestConversation(CONVERSATION_ID, USER_ID, "测试会话", "normal");
            when(conversationMapper.selectOne(any())).thenReturn(conv);

            List<ChatMessage> messages = Arrays.asList(
                    createTestMessage("msg1", CONVERSATION_ID, "user", "你好"),
                    createTestMessage("msg2", CONVERSATION_ID, "assistant", "你好！")
            );
            when(chatMessageMapper.selectList(any())).thenReturn(messages);

            List<ChatMessage> result = conversationService.getConversationDetail(CONVERSATION_ID, USER_ID);

            assertEquals(2, result.size());
            assertEquals("你好", result.get(0).getContent());
            assertEquals("你好！", result.get(1).getContent());
        }

        @Test
        @DisplayName("会话不属于当前用户应返回空列表")
        void shouldReturnEmptyListForUnauthorizedAccess() {
            when(conversationMapper.selectOne(any())).thenReturn(null);

            List<ChatMessage> result = conversationService.getConversationDetail(CONVERSATION_ID, USER_ID);

            assertTrue(result.isEmpty());
            verify(chatMessageMapper, never()).selectList(any());
        }
    }

    @Nested
    @DisplayName("deleteConversation - 删除会话")
    class DeleteConversationTests {

        @Test
        @DisplayName("应删除会话及其所有消息")
        void shouldDeleteConversationAndMessages() {
            Conversation conv = createTestConversation(CONVERSATION_ID, USER_ID, "测试", "normal");
            when(conversationMapper.selectOne(any())).thenReturn(conv);
            when(chatMessageMapper.delete(any())).thenReturn(3);

            assertDoesNotThrow(() ->
                    conversationService.deleteConversation(CONVERSATION_ID, USER_ID));

            verify(chatMessageMapper).delete(any());
            verify(conversationMapper).deleteById(CONVERSATION_ID);
        }

        @Test
        @DisplayName("删除不存在的会话不应抛出异常")
        void shouldNotThrowForNonExistentConversation() {
            when(conversationMapper.selectOne(any())).thenReturn(null);

            assertDoesNotThrow(() ->
                    conversationService.deleteConversation("nonexistent", USER_ID));

            verify(conversationMapper, never()).deleteById(anyString());
        }
    }

    @Nested
    @DisplayName("clearConversationMessages - 清空会话消息")
    class ClearConversationMessagesTests {

        @Test
        @DisplayName("应清空会话的所有消息")
        void shouldClearAllMessages() {
            Conversation conv = createTestConversation(CONVERSATION_ID, USER_ID, "测试", "normal");
            when(conversationMapper.selectOne(any())).thenReturn(conv);

            assertDoesNotThrow(() ->
                    conversationService.clearConversationMessages(CONVERSATION_ID, USER_ID));

            verify(chatMessageMapper).delete(any());
        }
    }

    @Disabled("需要 MyBatis Plus Lambda 缓存上下文，在集成测试中验证")
    @Nested
    @DisplayName("saveUserMessage - 保存用户消息")
    class SaveUserMessageTests {

        @Test
        @DisplayName("应成功保存用户消息")
        void shouldSaveUserMessage() {
            when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);

            assertDoesNotThrow(() ->
                    conversationService.saveUserMessage(CONVERSATION_ID, USER_ID,
                            "你好，AI！", "msg_user_001"));

            verify(chatMessageMapper).insert(any(ChatMessage.class));
            verify(conversationMapper).update(any(), any());
        }
    }

    @Disabled("需要 MyBatis Plus Lambda 缓存上下文，在集成测试中验证")
    @Nested
    @DisplayName("saveAssistantMessage - 保存AI回复")
    class SaveAssistantMessageTests {

        @Test
        @DisplayName("应保存AI回复并记录Token消耗")
        void shouldSaveAssistantMessageAndRecordToken() {
            when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);

            conversationService.saveAssistantMessage(CONVERSATION_ID, USER_ID,
                    "你好！我是AI助手。", "msg_ai_001", 150);

            verify(chatMessageMapper).insert(any(ChatMessage.class));
            verify(titleGenerationService).checkAndGenerateTitle(eq(CONVERSATION_ID), eq(USER_ID));
        }

        @Test
        @DisplayName("tokenCount 为 null 时不应记录Token消耗")
        void shouldNotRecordTokenWhenNull() {
            when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);

            conversationService.saveAssistantMessage(CONVERSATION_ID, USER_ID,
                    "你好！", "msg_ai_002", null);

            verify(chatMessageMapper).insert(any(ChatMessage.class));
            verify(tokenUsageMapper, never()).selectByUserIdAndDate(anyString(), any());
        }

        @Test
        @DisplayName("tokenCount 为 0 时不应记录Token消耗")
        void shouldNotRecordTokenWhenZero() {
            when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);

            conversationService.saveAssistantMessage(CONVERSATION_ID, USER_ID,
                    "回复", "msg_ai_003", 0);

            verify(chatMessageMapper).insert(any(ChatMessage.class));
            verify(tokenUsageMapper, never()).selectByUserIdAndDate(anyString(), any());
        }
    }

    @Disabled("需要 MyBatis Plus Lambda 缓存上下文，在集成测试中验证")
    @Nested
    @DisplayName("updateConversationTitle - 更新会话标题")
    class UpdateConversationTitleTests {

        @Test
        @DisplayName("应成功更新标题并标记为手动修改")
        void shouldUpdateTitleAndMarkManual() {
            assertDoesNotThrow(() ->
                    conversationService.updateConversationTitle(CONVERSATION_ID, USER_ID, "自定义标题"));

            verify(conversationMapper).update(any(), any());
        }
    }

    @Disabled("需要 MyBatis Plus Lambda 缓存上下文，在集成测试中验证")
    @Nested
    @DisplayName("updateConversationPinned - 置顶/取消置顶")
    class UpdateConversationPinnedTests {

        @Test
        @DisplayName("应成功置顶会话")
        void shouldPinConversation() {
            Conversation conv = createTestConversation(CONVERSATION_ID, USER_ID, "测试", "normal");
            when(conversationMapper.selectOne(any())).thenReturn(conv);

            conversationService.updateConversationPinned(CONVERSATION_ID, USER_ID, true);

            verify(conversationMapper).update(any(), any());
        }

        @Test
        @DisplayName("应成功取消置顶")
        void shouldUnpinConversation() {
            Conversation conv = createTestConversation(CONVERSATION_ID, USER_ID, "测试", "normal");
            when(conversationMapper.selectOne(any())).thenReturn(conv);

            conversationService.updateConversationPinned(CONVERSATION_ID, USER_ID, false);

            verify(conversationMapper).update(any(), any());
        }

        @Test
        @DisplayName("会话不属于当前用户不应执行操作")
        void shouldNotModifyForUnauthorizedUser() {
            when(conversationMapper.selectOne(any())).thenReturn(null);

            conversationService.updateConversationPinned(CONVERSATION_ID, USER_ID, true);

            verify(conversationMapper, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("batchDeleteConversations - 批量删除")
    class BatchDeleteConversationsTests {

        @Test
        @DisplayName("应批量删除多个会话")
        void shouldBatchDeleteConversations() {
            Conversation conv1 = createTestConversation("c1", USER_ID, "会话1", "normal");
            Conversation conv2 = createTestConversation("c2", USER_ID, "会话2", "normal");

            when(conversationMapper.selectOne(any()))
                    .thenReturn(conv1)  // 第一次调用返回 conv1
                    .thenReturn(conv2); // 第二次调用返回 conv2
            when(chatMessageMapper.delete(any())).thenReturn(2);

            conversationService.batchDeleteConversations(Arrays.asList("c1", "c2"), USER_ID);

            verify(conversationMapper, times(2)).deleteById(anyString());
            verify(chatMessageMapper, times(2)).delete(any());
        }

        @Test
        @DisplayName("空列表不应执行任何操作")
        void shouldDoNothingForEmptyList() {
            conversationService.batchDeleteConversations(Collections.emptyList(), USER_ID);

            verify(conversationMapper, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("null 列表不应执行任何操作")
        void shouldDoNothingForNullList() {
            conversationService.batchDeleteConversations(null, USER_ID);

            verify(conversationMapper, never()).deleteById(anyString());
        }
    }
}
