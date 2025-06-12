package com.hd.hd_backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeightWebSocketHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WS_URI = "ws://localhost:8080/hd/websocket";

    private CompletableFuture<String> messageFuture;

    @BeforeEach
    void setup() {
        messageFuture = new CompletableFuture<>();
    }

    @Test
    public void testAddWeightSuccess() throws Exception {
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        WebSocketSession session = new StandardWebSocketClient().doHandshake(
                new AbstractWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        messageQueue.offer(message.getPayload());  // 每条消息都加入队列
                    }
                },
                null,
                URI.create(WS_URI)
        ).get();

        // 登录和发送体重记录
        session.sendMessage(new TextMessage("login:{\"password\":\"a03c32fcd351cba2d9738622b083bed022ef07793bd92b59faea0207653f371d\",\"phone\":\"111\"}"));
        session.sendMessage(new TextMessage("addWeight:54"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应（addWeight）: " + message2);

        assertNotNull(message2, "未收到体重添加的响应");
        assertTrue(message2.contains("体重记录添加成功"));
    }


    @Test
    public void testAddWeightNotLogin() throws Exception {
        WebSocketSession session = new StandardWebSocketClient().doHandshake(
                new AbstractWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        messageFuture.complete(message.getPayload());
                    }
                },
                null,
                URI.create(WS_URI)
        ).get();
        session.sendMessage(new TextMessage("addWeight:54"));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("用户未登录"));
    }


}
