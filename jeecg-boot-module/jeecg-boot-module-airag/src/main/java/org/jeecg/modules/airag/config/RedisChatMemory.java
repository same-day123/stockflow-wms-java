package org.jeecg.modules.airag.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.airag.app.consts.AiAppConsts;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.service.IAiragAppService;
import org.jeecg.modules.airag.app.service.IAiragChatService;
import org.jeecg.modules.airag.app.vo.ChatConversation;
import org.jeecg.modules.airag.app.vo.ChatSendParams;
import org.jeecg.modules.airag.common.handler.IAIChatHandler;
import org.jeecg.modules.airag.common.vo.MessageHistory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedisChatMemory implements ChatMemory {

    @Autowired
    private  RedisTemplate redisTemplate;

    private final static String PREFIX = "chat:";
    @Autowired
    private IAiragChatService airagChatService;
    @Autowired
    private IAiragAppService airagAppService;
    @Autowired
    private IAIChatHandler aiChatHandler;

    //添加消息
    @Override
    public void add(String chatSendParamsJson, List<Message> messagesList) {
        if (messagesList == null || messagesList.isEmpty()) {
            return;
        }
        //解析请求参数，SpringAIMsg是按照Spring AI消息内容定义的
        List<SpringAIMsg> msgs = messagesList.stream().map(SpringAIMsg::new).toList();
        SpringAIMsg msg = msgs.get(0);
        //获取用户消息
        String userMessage = msg.getText();
        //获取消息类型 user/assistant
        MessageType messageType = msg.getMessageType();
        //解析请求参数,传入的参数是json字符串
        ChatSendParams chatSendParams = JSON.parseObject(chatSendParamsJson, ChatSendParams.class);
        //获取会话id
        String conversationId = chatSendParams.getConversationId();
        String topicId = chatSendParams.getTopicId();
        //获取用户名
        String username = chatSendParams.getUsername();
        // 获取app信息
        AiragApp app = null;
        if (oConvertUtils.isNotEmpty(chatSendParams.getAppId())) {
            app = airagAppService.getById(chatSendParams.getAppId());
        }
        //redis key
        String redisKey = "airag:chat:"+username+":" + conversationId;
        if (oConvertUtils.isObjectEmpty(app)) {
            app = new AiragApp();
            app.setId(AiAppConsts.DEFAULT_APP_ID);
        }
        ChatConversation chatConversation = null;
        if (oConvertUtils.isNotEmpty(redisKey)) {
            chatConversation = (ChatConversation) redisTemplate.boundValueOps(redisKey).get();
        }
        if (null == chatConversation) {
            chatConversation = createConversation(conversationId);
        }
        chatConversation.setApp(app);
        // 更新标题
        if (oConvertUtils.isEmpty(chatConversation.getTitle())) {
            chatConversation.setTitle(userMessage.length() > 5 ? userMessage.substring(0, 5) : userMessage);
        }

        // 从历史消息中组装本次的消息列表
        List<ChatMessage> messages = airagChatService.collateMessage(chatConversation, topicId);

        ChatMessage chatMessage = null;
        if(messageType == MessageType.USER){
            chatMessage = aiChatHandler.buildUserMessage(userMessage, null);
        }else{
            chatMessage = new AiMessage(userMessage);
        }
        // 追加消息
        airagChatService.appendMessage(messages, chatMessage, chatConversation, topicId);

        BoundValueOperations chatRedisCacheOp = redisTemplate.boundValueOps(redisKey);
        chatRedisCacheOp.set(chatConversation);

    }

    //创建新会话
    private ChatConversation createConversation(String conversationId) {
        // 新会话
        conversationId = oConvertUtils.getString(conversationId, UUIDGenerator.generate());
        ChatConversation chatConversation = new ChatConversation();
        chatConversation.setId(conversationId);
        chatConversation.setCreateTime(new Date());
        return chatConversation;
    }


    @Override
    public List<Message> get(String chatSendParamsJson, int lastN) {
        //解析请求参数
        ChatSendParams chatSendParams = JSON.parseObject(chatSendParamsJson, ChatSendParams.class);
        String conversationId = chatSendParams.getConversationId();
        String topicId = chatSendParams.getTopicId();
        String username = chatSendParams.getUsername();
        //redis key
        String redisKey = "airag:chat:"+username+":" + conversationId;
        ChatConversation chatConversation = null;
        if (oConvertUtils.isNotEmpty(redisKey)) {
            chatConversation = (ChatConversation) redisTemplate.boundValueOps(redisKey).get();
        }
        if(chatConversation != null){
            List<MessageHistory> messagesHistory = chatConversation.getMessages();
            List<Message> collect = messagesHistory.stream().map(msg -> {
                 return switch (msg.getRole()) {
                    case "user" -> new UserMessage(msg.getContent(), List.of(), Map.of());
                    case "ai" -> new AssistantMessage(msg.getContent(), Map.of(), List.of(), List.of());
                    default -> throw new IllegalArgumentException("Unsupported message type: " + msg.getRole());
                } ;
            }).collect(Collectors.toList());
            return collect;
        }
        return List.of();

    }

    //此方法暂时不用，清除会话使用jeecgboot原有接口
    @Override
    public void clear(String chatSendParamsJson) {

        //解析请求参数
        ChatSendParams chatSendParams = JSON.parseObject(chatSendParamsJson, ChatSendParams.class);
        String conversationId = chatSendParams.getConversationId();
        String topicId = chatSendParams.getTopicId();
        String username = chatSendParams.getUsername();
        //redis key
        String redisKey = "airag:chat:"+username+":" + conversationId;

        redisTemplate.delete(redisKey);

    }
}
