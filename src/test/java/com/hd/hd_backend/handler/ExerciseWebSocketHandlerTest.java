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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExerciseWebSocketHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WS_URI = "ws://localhost:8080/hd/websocket";

    private CompletableFuture<String> messageFuture;

    @BeforeEach
    void setup() {
        messageFuture = new CompletableFuture<>();
    }

    @Test
    public void testGetExerciseItem() throws Exception {
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
        session.sendMessage(new TextMessage("getAllExerciseItem"));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("\"code\":")); // 可继续判断结构内容
        assertTrue(response.contains("data"));
    }
    @Test
    public void testAddExerciseRecordSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("addExerciseRecord:{" +
                "\"exerciseId\": \"3\"," +
                "\"duration\": \"01:15:30\"," +
                "\"date\": \"2025-05-15T14:30:00\"" +
                "}"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("success"));
    }

    @Test
    public void testAddExerciseRecordMissingExerciseId() throws Exception {
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
        session.sendMessage(new TextMessage("addExerciseRecord:{" +
                "\"duration\": \"01:15:30\"," +
                "\"date\": \"2025-05-15T14:30:00\"" +
                "}"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("添加失败"));
    }
    @Test
    public void testAddExerciseRecordWrongExerciseId() throws Exception {
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
        session.sendMessage(new TextMessage("addExerciseRecord:{" +
                "\"exerciseId\": \"1000\"," +
                "\"duration\": \"01:15:30\"," +
                "\"date\": \"2025-05-15T14:30:00\"" +
                "}"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("添加失败"));
    }
    @Test
    public void testAddExerciseRecordNotLogin() throws Exception {
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
        session.sendMessage(new TextMessage("addExerciseRecord:{" +
                "\"exerciseId\": \"3\"," +
                "\"duration\": \"01:15:30\"," +
                "\"date\": \"2025-05-15T14:30:00\"" +
                "}"));

        // 等待并收集两条消息
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("用户未登录"));
    }
    @Test
    public void testGetUserExerciseRecordSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("getUserExerciseRecord:"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("\"code\":"));
        assertTrue(message2.contains("\"data\""));
        assertTrue(message2.contains("exerciseRecordId"));
    }
    @Test
    public void testGetUserExerciseRecordNotLogin() throws Exception {
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
        session.sendMessage(new TextMessage("getUserExerciseRecord:"));

        // 等待并收集两条消息
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        System.out.println("第二条响应: " + message2);
        assertTrue(message2.contains("请先登录"));
    }

    @Test
    public void testDeleteExerciseRecord() throws Exception {
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
        session.sendMessage(new TextMessage("deleteExerciseRecord:12"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("运动记录删除成功"));
    }
    @Test
    public void testDeleteExerciseRecordMissingRecordId() throws Exception {
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
        session.sendMessage(new TextMessage("deleteExerciseRecord:"));

        // 等待并收集两条消息
        String message1 = messageQueue.poll(5, TimeUnit.SECONDS);
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第一条响应: " + message1);
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("运动记录删除失败"));
    }

    @Test
    public void testDeleteExerciseRecordNotLogin() throws Exception {
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
        session.sendMessage(new TextMessage("deleteExerciseRecord:"));

        // 等待并收集两条消息
        String message2 = messageQueue.poll(5, TimeUnit.SECONDS);

        // 打印并断言第二条（即“最后一条”）是 addWeight 成功的返回
        System.out.println("第二条响应: " + message2);

        assertTrue(message2.contains("用户未登录"));
    }
}
