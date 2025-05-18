package com.hd.hd_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;
import com.hd.hd_backend.handler.UserWebSocketHandler;
import com.hd.hd_backend.service.UserService;
import com.hd.hd_backend.utils.JsonUtils;
import com.hd.hd_backend.utils.WebSocketCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.AbstractWebSocketSession;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class WebSocketHandlerIntegrationTest {
    @Autowired
    private UserWebSocketHandler userWebSocketHandler;

    @MockBean
    private UserService userService;

   // private WebSocketSession session;
    private MockWebSocketSession session;
    @BeforeEach
    public void setup() {
        session = new MockWebSocketSession();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // 构造模拟登录用户
        User loginUser = new User();
        loginUser.setPhone("12345678901");
        loginUser.setPassword("password");
        loginUser.setId(1);
        loginUser.setIsAdmin(0);

        // mock login 行为
        Mockito.when(userService.login(Mockito.any(User.class))).thenReturn(loginUser);

        // 构造登录消息：格式为 "login:{json}"
        String json = JsonUtils.toJson(loginUser);
        TextMessage message = new TextMessage("login:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        // 验证 session 中是否设置 userId
        Assertions.assertEquals(1, session.getAttributes().get("userId"));

        // 验证返回的消息是否为登录成功
        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());
        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.LOGIN_SUCCESS.ordinal()));

        Assertions.assertTrue(response.contains("\"data\""));
    }

    @Test
    public void testLoginFailWrongPassword() throws Exception {
        // 准备异常模拟
        Mockito.when(userService.login(Mockito.any(User.class)))
                .thenThrow(new Exception("密码错误"));

        User loginUser = new User();
        loginUser.setPhone("12345678901");
        loginUser.setPassword("wrongpassword");

        String json = JsonUtils.toJson(loginUser);
        TextMessage message = new TextMessage("login:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());
        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.LOGIN_FAIL.ordinal()));
        Assertions.assertTrue(response.contains("密码错误"));
    }
    @Test
    public void testLoginFailPhoneEmpty() throws Exception {
        Mockito.when(userService.login(Mockito.any(User.class)))
                .thenThrow(new Exception("手机号不能为空"));

        User loginUser = new User();
        loginUser.setPhone(""); // 空手机号
        loginUser.setPassword("somePassword");

        String json = JsonUtils.toJson(loginUser);
        TextMessage message = new TextMessage("login:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());
        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.LOGIN_FAIL.ordinal()));
        Assertions.assertTrue(response.contains("手机号不能为空"));
    }
    @Test
    public void testLoginFailPasswordEmpty() throws Exception {
        Mockito.when(userService.login(Mockito.any(User.class)))
                .thenThrow(new Exception("密码不能为空"));

        User loginUser = new User();
        loginUser.setPhone("12345678901");
        loginUser.setPassword(""); // 空密码

        String json = JsonUtils.toJson(loginUser);
        TextMessage message = new TextMessage("login:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());
        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.LOGIN_FAIL.ordinal()));
        Assertions.assertTrue(response.contains("密码不能为空"));
    }
    @Test
    public void testLoginFailUserNotFound() throws Exception {
        Mockito.when(userService.login(Mockito.any(User.class)))
                .thenThrow(new Exception("用户不存在"));

        User loginUser = new User();
        loginUser.setPhone("00000000000"); // 不存在的用户
        loginUser.setPassword("anyPassword");

        String json = JsonUtils.toJson(loginUser);
        TextMessage message = new TextMessage("login:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());
        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.LOGIN_FAIL.ordinal()));
        Assertions.assertTrue(response.contains("用户不存在"));
    }
    @Test
    public void testRegisterSuccess() throws Exception {
        // 准备数据和模拟
        NormalUser normalUser = new NormalUser();
        normalUser.setPhone("12345678901");
        normalUser.setPassword("password123");
        normalUser.setWeight(70.0); // 假设提供了体重

        // 模拟 userService.register 正常执行
        Mockito.when(userService.register(Mockito.any(NormalUser.class)))
                .thenReturn(normalUser); // 返回注册的用户

        String json = JsonUtils.toJson(normalUser);
        TextMessage message = new TextMessage("register:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());

        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.REGISTER_SUCCESS.ordinal()));
        Assertions.assertTrue(response.contains("user"));

        // 验证用户信息
        Assertions.assertTrue(response.contains(normalUser.getPhone()));
    }
    @Test
    public void testRegisterFailPhoneExists() throws Exception {
        // 准备数据
        NormalUser normalUser = new NormalUser();
        normalUser.setPhone("12345678901"); // 假设这个手机号已存在
        normalUser.setPassword("password123");

        // 模拟 userService.register 失败（手机号已存在）
        Mockito.when(userService.register(Mockito.any(NormalUser.class)))
                .thenThrow(new Exception("手机号已存在"));

        String json = JsonUtils.toJson(normalUser);
        TextMessage message = new TextMessage("register:" + json);

        userWebSocketHandler.handleTextMessage(session, message);

        List<TextMessage> messages = session.getSentMessages();
        Assertions.assertFalse(messages.isEmpty());

        String response = messages.get(0).getPayload();
        Assertions.assertTrue(response.contains("\"code\":" + WebSocketCode.REGISTER_FAIL.ordinal()));
        Assertions.assertTrue(response.contains("手机号已存在"));
    }

    /**
     * 模拟 WebSocketSession 的简单实现，可用于收集发送的消息
     */
    public class MockWebSocketSession implements WebSocketSession {

        private final Map<String, Object> attributes = new HashMap<>();
        private final List<TextMessage> sentMessages = new ArrayList<>();

        @Override
        public void sendMessage(WebSocketMessage<?> message) {
            if (message instanceof TextMessage) {
                sentMessages.add((TextMessage) message);
            }
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public List<TextMessage> getSentMessages() {
            return sentMessages;
        }

        // 下面是 WebSocketSession 中必须实现的方法（只实现你用到的部分，其余抛出异常）
        @Override
        public String getId() {
            return "mock-session";
        }

        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public HttpHeaders getHandshakeHeaders() {
            return null;
        }

        @Override
        public Principal getPrincipal() {
            return null;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getAcceptedProtocol() {
            return null;
        }

        @Override
        public void setTextMessageSizeLimit(int messageSizeLimit) {

        }

        @Override
        public int getTextMessageSizeLimit() {
            return 0;
        }

        @Override
        public void setBinaryMessageSizeLimit(int messageSizeLimit) {

        }

        @Override
        public int getBinaryMessageSizeLimit() {
            return 0;
        }

        @Override
        public List<WebSocketExtension> getExtensions() {
            return List.of();
        }

        @Override
        public void close() {
        }

        @Override
        public void close(CloseStatus status) {
        }

        @Override
        public boolean isOpen() {
            return true;
        }

    }

}
