package org.jeecg.modules.wms.ai.chat.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.*;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.service.IAiragAppService;
import org.jeecg.modules.airag.app.vo.ChatSendParams;
import org.jeecg.modules.wms.ai.chat.tools.WmsInventoryTools;
import org.jeecg.modules.wms.ai.chat.util.VectorDistanceUtils;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {


    @Resource(name = "chatClientOpenAi") //使用openai
//    @Resource(name = "chatClientOllama") //使用ollama
    public ChatClient chatClient;

    @Autowired
    private IAiragAppService airagAppService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private WmsInventoryTools wmsInventoryTools;

    // 请求方式和路径不要改动，将来要与前端联调
//    @RequestMapping("/chat")
//    public String chat(String prompt) {
//        return chatClient
//                .prompt(prompt) // 传入user提示词
//                .call() // 同步请求，会等待AI全部输出完才返回结果
//                .content(); //返回响应内容
//    }


    // 获取当前用户名
    private String getUsername(HttpServletRequest httpRequest) {
        try {
            TokenUtils.getTokenByRequest();
            String token;
            if(null != httpRequest){
                token = TokenUtils.getTokenByRequest(httpRequest);
            }else{
                token = TokenUtils.getTokenByRequest();
            }
            if (TokenUtils.verifyToken(token, sysBaseApi, redisUtil)) {
                return JwtUtil.getUsername(token);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    //流式调用
    @RequestMapping(value = "/chat" , produces = "text/html;charset=UTF-8")//告诉浏览器返回的是什么样的字符集的数据
    public Flux<String> chat(String prompt) {
        Flux<String> content = chatClient
                .prompt(prompt) // 传入user提示词
                .stream()
                .content();//返回响应内容
        return content;
    }

    /**
     * 获取questionAnswerAdvisor 对象
     * @param knowIds 知识库ids
     * @return
     */
    private QuestionAnswerAdvisor getQuestionAnswerAdvisor(List<String>  knowIds) {
        // 将knowIds转换成字符串
        StringBuilder stringBuilder = new StringBuilder();
        // 遍历knowIds
        Iterator<String> stringIterator = knowIds.iterator();

        while (stringIterator.hasNext()) {
            String knowId = stringIterator.next();
            // 拼接字符串
            stringBuilder.append("knowledgeId == '").append(knowId).append("'");
            if (stringIterator.hasNext()) {
                stringBuilder.append(" || ");
            }
        }
        // springAI提供的实现RAG的方法
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(
                pgVectorStore, // 向量库
                SearchRequest.builder() // 向量检索的请求参数
                        .similarityThreshold(0.5d) // 相似度阈值
                        .filterExpression(stringBuilder.toString())
                        .topK(2) // 返回的文档片段数量
                        .build());
        return questionAnswerAdvisor;
    }

    /**
     * 发送消息给前端
     */
    @RequestMapping(value = "/chat/send")
    public SseEmitter send(@RequestBody ChatSendParams chatSendParams, HttpServletRequest httpRequest) {
        // 参数校验
        AssertUtils.assertNotEmpty("参数异常", chatSendParams);
        String userMessage = chatSendParams.getContent();
        AssertUtils.assertNotEmpty("至少发送一条消息", userMessage);

        // 获取会话信息
        String conversationId = chatSendParams.getConversationId();//会话id
        String topicId = oConvertUtils.getString(chatSendParams.getTopicId(), UUIDGenerator.generate());
        String username = getUsername(httpRequest);
        chatSendParams.setUsername(username);
        String jsonString = JSONObject.toJSONString(chatSendParams);//转换成json

        // 每次会话都生成一个新的,用来缓存emitter
        String requestId = UUIDGenerator.generate();
        SseEmitter emitter = new SseEmitter(-0L);


        // 获取app信息
        AiragApp app = null;
        List<String> knowIds = null;
        if (oConvertUtils.isNotEmpty(chatSendParams.getAppId())) {
            app = airagAppService.getById(chatSendParams.getAppId());
            //获取知识库的ID
            knowIds = app.getKnowIds();
        }
        Flux<String> flux = null;

        //向大模型发送提示词
        if (app == null) {
            flux = chatClient
                    .prompt(userMessage) // 传入user提示词
                    .advisors(a->a.param(CHAT_MEMORY_CONVERSATION_ID_KEY,jsonString))
                    .stream()
                    .content();
        } else {
            if (knowIds != null && knowIds.size() > 0) {
                QuestionAnswerAdvisor questionAnswerAdvisor = this.getQuestionAnswerAdvisor(knowIds);
                flux = chatClient
                    .prompt(userMessage) // 传入user提示词
                    .user(userMessage)//用户提示词
                    .system(app.getPrompt())//系统提示词
                    .advisors(a->a.param(CHAT_MEMORY_CONVERSATION_ID_KEY,jsonString))
                    .advisors(questionAnswerAdvisor)
                    .tools(wmsInventoryTools)
                    .stream()
                    .content();

            }
            flux = chatClient
                    .prompt(userMessage) // 传入user提示词
                    .user(userMessage)//用户提示词
                    .system(app.getPrompt())//系统提示词
                    .advisors(a->a.param(CHAT_MEMORY_CONVERSATION_ID_KEY,jsonString))
                    .tools(wmsInventoryTools)
                    .stream()
                    .content();
        }

        /**
         * 是否正在思考
         */
        AtomicBoolean isThinking = new AtomicBoolean(false);

        // 发送数据给前端
        try {
            sendMessage(emitter, conversationId, topicId, requestId, ">", "MESSAGE");
            sendMessage(emitter, conversationId, topicId, requestId, "\n", "MESSAGE");
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // ai聊天响应
        flux.subscribe(
                data -> {
                    // 兼容推理模型
                    if ("<think>".equals(data)) {
                        isThinking.set(true);
                        data = "> ";
                    }
                    if ("</think>".equals(data)) {
                        isThinking.set(false);
                        data = "\n\n";
                    }
                    if (isThinking.get()) {
                        if (null != data && data.contains("\n")) {
                            data = "\n> ";
                        }
                    }
                    // 发送数据给前端
                    try {
                        sendMessage(emitter, conversationId, topicId, requestId, data, "MESSAGE");
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    // 发送错误给前端
                    emitter.completeWithError(error);
                },
                () -> {
                    // 发送完成给前端
                    try {
                        sendEndMessage(emitter, conversationId, topicId, requestId);
                        sendEndMessage(emitter, conversationId, topicId, requestId);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }
        );
        return emitter;
    }
    // 辅助方法：向客户端发送消息
    private void sendMessage(SseEmitter emitter, String conversationId, String topicId,
                             String requestId, String message,String event) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", conversationId);
        response.put("topicId", topicId);
        response.put("requestId", requestId);
        response.put("event", event);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        response.put("data", messageData);

        String jsonData =  JSONObject.toJSONString(response);
        emitter.send(SseEmitter.event().data(jsonData));
    }
    // 辅助方法：发送结束消息
    private void sendEndMessage(SseEmitter emitter, String conversationId, String topicId,
                                String requestId) throws IOException {
        Map<String, Object> endResponse = new HashMap<>();
        endResponse.put("event", "MESSAGE_END");
        endResponse.put("flowId", null);
        endResponse.put("requestId", requestId);
        endResponse.put("conversationId", conversationId);
        endResponse.put("topicId", topicId);
        endResponse.put("data", null);

        String endJson =  JSONObject.toJSONString(endResponse);
        emitter.send(SseEmitter.event().data(endJson));
    }


    @Resource
    private OpenAiEmbeddingModel openAiEmbeddingModel;

    @Resource(name = "pgVectorStore")
    private VectorStore pgVectorStore;

    @GetMapping(value = "/embedtest")
    public String embedtest(String query, String t1, String t2) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("query: " + query);
        sb.append("</br>");
        // 先将查询文本向量化
        float[] queryVector = openAiEmbeddingModel.embed(query);
        sb.append("query向量: " + arrayToString(queryVector));
        sb.append("</br>");
        sb.append("==================================");
        sb.append("</br>");
        sb.append("t1: " + t1);
        sb.append("</br>");
        float[] embed1 = openAiEmbeddingModel.embed(t1);
        sb.append("t1向量: " + arrayToString(embed1));
        sb.append("</br>");
        sb.append("相似度,比较比较欧氏距离: " + VectorDistanceUtils.euclideanDistance(queryVector, embed1));
        sb.append("</br>");
        sb.append("相似度,比较比较余弦距离: " + VectorDistanceUtils.cosineDistance(queryVector, embed1));
        sb.append("</br>");
        sb.append("==================================");
        sb.append("</br>");
        sb.append("t2: " + t2);
        sb.append("</br>");
        float[] embed2 = openAiEmbeddingModel.embed(t2);
        sb.append("t2向量: " + arrayToString(embed2));
        sb.append("</br>");
        sb.append("相似度,比较比较欧氏距离: " + VectorDistanceUtils.euclideanDistance(queryVector, embed2));
        sb.append("</br>");
        sb.append("相似度,比较比较余弦距离: " + VectorDistanceUtils.cosineDistance(queryVector, embed2));
        //向向量数据库写入两个文档
        Document document = new Document(t1);
        Document document2 = new Document(t2);
        //将文档放入List 中
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(document);
        documents.add(document2);
        //借助向量模型生成向量，然后借助向量表添加到向量数据库中
        pgVectorStore.add(documents);

        return sb.toString();
    }

    // 辅助方法：将float数组格式化为字符串
    private String arrayToString(float[] array) {
        if (array == null) return "null";
        if (array.length == 0) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        // 限制显示长度，避免输出过长的向量
        int limit = Math.min(array.length, 10); // 只显示前10个元素
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%.6f", array[i]));
            if (i < limit - 1) {
                sb.append(", ");
            }
        }

        if (array.length > limit) {
            sb.append(", ... (").append(array.length).append(" dimensions)");
        }

        sb.append("]");
        return sb.toString();
    }
}