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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FoodWebSocketHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WS_URI = "ws://localhost:8080/hd/websocket";

    private CompletableFuture<String> messageFuture;

    @BeforeEach
    void setup() {
        messageFuture = new CompletableFuture<>();
    }

    @Test
    public void testGetAllFood() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        session.sendMessage(new TextMessage("getAllFood:"));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("\"code\":")); // 可继续判断结构内容
        assertTrue(response.contains("data"));
    }
    @Test
    public void testGetFoodByNameSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String foodName = "小米";
        String message = "getFoodByName:" + foodName;
        session.sendMessage(new TextMessage(message));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        assertTrue(response.contains("小米"));
    }
    @Test
    public void testGetFoodByNameNotFound() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String foodName = "苹果";
        String message = "getFoodByName:" + foodName;
        session.sendMessage(new TextMessage(message));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("食物未找到"));
    }
    @Test
    public void testGetFoodByNameMissingParameter() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String message = "getFoodByName:"; // 缺少参数
        session.sendMessage(new TextMessage(message));

        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertNotNull(response);
        assertTrue(response.contains("缺少食物名称参数"));
    }
    @Test
    public void testAddFoodRecordSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String formattedDateTime = "2025-05-10T13:25:00";
        String userId = "25";
        int foodId = 1;
        double weight = 120.0;
        double sumCalories = 350.5;
        double sumFat = 10.2;
        double sumProtein = 15.4;
        double sumCarbohydrates = 45.1;
        double sumSodium = 230.5;
        double sumPotassium = 310.8;
        double sumDietaryFiber = 5.5;

        String addMessage = String.format(
                "addFoodRecord:{\"recordTime\":\"%s\",\"userId\":\"%s\",\"foodId\":%d," +
                        "\"foodWeight\":%.2f,\"calories\":%.2f,\"fat\":%.2f,\"protein\":%.2f," +
                        "\"carbohydrates\":%.2f,\"sodium\":%.2f,\"potassium\":%.2f," +
                        "\"dietaryFiber\":\"%.2f\"}",
                formattedDateTime,
                userId,
                foodId,
                weight,
                sumCalories,
                sumFat,
                sumProtein,
                sumCarbohydrates,
                sumSodium,
                sumPotassium,
                sumDietaryFiber
        );

        // 发送消息
        session.sendMessage(new TextMessage(addMessage));
        System.out.println("Sending addFoodRecord message: " + addMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);

        // 验证返回值是否包含成功的标识符
        assertNotNull(response); // 确保收到响应
        assertTrue(response.contains("食物记录添加成功")); // 验证响应中包含添加成功的消息
    }
    @Test
    public void testAddFoodRecordMissingFoodId() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String formattedDateTime = "2025-05-10T13:25:00";
        String userId = "25";
        double weight = 120.0;
        double sumCalories = 350.5;
        double sumFat = 10.2;
        double sumProtein = 15.4;
        double sumCarbohydrates = 45.1;
        double sumSodium = 230.5;
        double sumPotassium = 310.8;
        double sumDietaryFiber = 5.5;

        String addMessage = String.format(
                "addFoodRecord:{\"recordTime\":\"%s\",\"userId\":\"%s\"," +
                        "\"foodWeight\":%.2f,\"calories\":%.2f,\"fat\":%.2f,\"protein\":%.2f," +
                        "\"carbohydrates\":%.2f,\"sodium\":%.2f,\"potassium\":%.2f," +
                        "\"dietaryFiber\":\"%.2f\"}",
                formattedDateTime,
                userId,
                weight,
                sumCalories,
                sumFat,
                sumProtein,
                sumCarbohydrates,
                sumSodium,
                sumPotassium,
                sumDietaryFiber
        );

        // 发送消息
        session.sendMessage(new TextMessage(addMessage));
        System.out.println("Sending addFoodRecord message: " + addMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("缺少 foodId 字段"));
        assertTrue(response.contains("error_message"));
    }
    @Test
    public void testAddFoodRecordMissingUserId() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String formattedDateTime = "2025-05-10T13:25:00";
        int foodId = 1;
        double weight = 120.0;
        double sumCalories = 350.5;
        double sumFat = 10.2;
        double sumProtein = 15.4;
        double sumCarbohydrates = 45.1;
        double sumSodium = 230.5;
        double sumPotassium = 310.8;
        double sumDietaryFiber = 5.5;

        String addMessage = String.format(
                "addFoodRecord:{\"recordTime\":\"%s\",\"foodId\":%d," +
                        "\"foodWeight\":%.2f,\"calories\":%.2f,\"fat\":%.2f,\"protein\":%.2f," +
                        "\"carbohydrates\":%.2f,\"sodium\":%.2f,\"potassium\":%.2f," +
                        "\"dietaryFiber\":\"%.2f\"}",
                formattedDateTime,
                foodId,
                weight,
                sumCalories,
                sumFat,
                sumProtein,
                sumCarbohydrates,
                sumSodium,
                sumPotassium,
                sumDietaryFiber
        );

        // 发送消息
        session.sendMessage(new TextMessage(addMessage));
        System.out.println("Sending addFoodRecord message: " + addMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertTrue(response.contains("缺少 userId 字段"));
        assertTrue(response.contains("error_message"));
    }
    @Test
    public void testAddFoodRecordNotLogin() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOff\"}"));

        String formattedDateTime = "2025-05-10T13:25:00";
        String userId = "25";
        int foodId = 1;
        double weight = 120.0;
        double sumCalories = 350.5;
        double sumFat = 10.2;
        double sumProtein = 15.4;
        double sumCarbohydrates = 45.1;
        double sumSodium = 230.5;
        double sumPotassium = 310.8;
        double sumDietaryFiber = 5.5;

        String addMessage = String.format(
                "addFoodRecord:{\"recordTime\":\"%s\",\"userId\":\"%s\",\"foodId\":%d," +
                        "\"foodWeight\":%.2f,\"calories\":%.2f,\"fat\":%.2f,\"protein\":%.2f," +
                        "\"carbohydrates\":%.2f,\"sodium\":%.2f,\"potassium\":%.2f," +
                        "\"dietaryFiber\":\"%.2f\"}",
                formattedDateTime,
                userId,
                foodId,
                weight,
                sumCalories,
                sumFat,
                sumProtein,
                sumCarbohydrates,
                sumSodium,
                sumPotassium,
                sumDietaryFiber
        );

        // 发送消息
        session.sendMessage(new TextMessage(addMessage));
        System.out.println("Sending addFoodRecord message: " + addMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);

        // 验证返回值是否包含成功的标识符
        assertNotNull(response); // 确保收到响应
        assertTrue(response.contains("用户未登录")); // 验证响应中包含添加失败的消息
    }
    @Test
    public void testDeleteFoodRecordSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String deleteFoodRecordMessage = "deleteFoodRecord:32";

        // 发送删除请求消息
        session.sendMessage(new TextMessage(deleteFoodRecordMessage));
        System.out.println("Sending deleteFoodRecord message: " + deleteFoodRecordMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);

        // 验证返回值是否包含成功的标识符
        assertNotNull(response); // 确保收到响应
        assertTrue(response.contains("食物记录删除成功")); // 验证响应中包含删除成功的消息
    }
    @Test
    public void testDeleteFoodRecordFailure() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String deleteFoodRecordMessage = "deleteFoodRecord:1000";

        // 发送删除请求消息
        session.sendMessage(new TextMessage(deleteFoodRecordMessage));
        System.out.println("Sending deleteFoodRecord message: " + deleteFoodRecordMessage);

        // 等待并接收返回消息
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);

        // 验证返回值是否包含成功的标识符
        assertNotNull(response); // 确保收到响应
        assertTrue(response.contains("删除食物记录失败")); // 验证响应中包含删除失败的消息
    }
    @Test
    public void testGetAllFoodRecordSuccess() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String requestMessage = "getAllFoodRecord:25";

        session.sendMessage(new TextMessage(requestMessage));
        String response = messageFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertTrue(response.contains("\"code\":"));
        assertTrue(response.contains("\"data\""));
        assertTrue(response.contains("recordTime")); // 根据 FoodRecordDTO 内容判断字段
    }
    @Test
    public void testGetAllFoodRecordMissingParameter() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOn\"}"));

        String requestMessage = "getAllFoodRecord:";

        session.sendMessage(new TextMessage(requestMessage));
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertNotNull(response);
        assertTrue(response.contains("缺少参数")); // 根据 FoodRecordDTO 内容判断字段
    }
    @Test
    public void testGetAllFoodRecordNotLogin() throws Exception {
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
        session.sendMessage(new TextMessage("{\"cmd\": \"testModeOff\"}"));

        String requestMessage = "getAllFoodRecord:25";

        session.sendMessage(new TextMessage(requestMessage));
        String response = messageFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response);
        assertNotNull(response);
        assertTrue(response.contains("用户未登录")); // 根据 FoodRecordDTO 内容判断字段
    }
}
