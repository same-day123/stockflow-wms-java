package org.jeecg.modules.airag.app.service;

import dev.langchain4j.data.message.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.vo.AppDebugParams;
import org.jeecg.modules.airag.app.vo.ChatConversation;
import org.jeecg.modules.airag.app.vo.ChatSendParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * ai聊天
 *
 * @author chenrui
 * @date 2025/2/25 13:36
 */
public interface IAiragChatService {

    /**
     * 发送消息
     *
     * @param chatSendParams
     * @return
     * @author chenrui
     * @date 2025/2/25 13:39
     */
    SseEmitter send(ChatSendParams chatSendParams);


    /**
     * 调试应用
     *
     * @param appDebugParams
     * @return
     * @author chenrui
     * @date 2025/2/28 10:49
     */
    SseEmitter debugApp(AppDebugParams appDebugParams);

    /**
     * 停止响应
     *
     * @param requestId
     * @return
     * @author chenrui
     * @date 2025/2/25 17:17
     */
    Result<?> stop(String requestId);

    /**
     * 获取所有对话
     *
     * @param appId
     * @return
     * @author chenrui
     * @date 2025/2/26 14:48
     */
    Result<?> getConversations(String appId);

    /**
     * 获取对话聊天记录
     *
     * @param conversationId
     * @return
     * @author chenrui
     * @date 2025/2/26 15:16
     */
    Result<?> getMessages(String conversationId);

    /**
     * 删除会话
     *
     * @param conversationId
     * @return
     * @author chenrui
     * @date 2025/3/3 16:55
     */
    Result<?> deleteConversation(String conversationId);

    /**
     * 更新会话标题
     * @param updateTitleParams
     * @return
     * @author chenrui
     * @date 2025/3/3 17:02
     */
    Result<?> updateConversationTitle(ChatConversation updateTitleParams);

    /**
     * 清空消息
     * @param conversationId
     * @return
     * @author chenrui
     * @date 2025/3/3 19:49
     */
    Result<?> clearMessage(String conversationId);

    /**
     * 追加消息
     *
     * @param messages
     * @param message
     * @param chatConversation
     * @param topicId
     * @return
     * @author chenrui
     * @date 2025/2/25 19:05
     */
    public void appendMessage(List<ChatMessage> messages, ChatMessage message, ChatConversation
            chatConversation, String topicId);

    /**
     * 构造消息
     *
     * @param conversation
     * @param topicId
     * @return
     * @author chenrui
     * @date 2025/2/25 15:26
     */
    public List<ChatMessage> collateMessage(ChatConversation conversation, String topicId);

    /**
     * 获取会话
     *
     * @param app
     * @param conversationId
     * @return
     * @author chenrui
     * @date 2025/2/25 19:19
     */
    @NotNull
    public ChatConversation getOrCreateChatConversation(AiragApp app, String conversationId);

    /**
     * 保存会话
     *
     * @param chatConversation
     * @param temp             是否临时会话
     * @author chenrui
     * @date 2025/2/25 19:27
     */
    public void saveChatConversation(ChatConversation chatConversation, boolean temp, HttpServletRequest httpRequest);
}
