package com.hd.hd_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;
import com.hd.hd_backend.handler.UserWebSocketHandler;
import com.hd.hd_backend.mapper.UserMapper;
import com.hd.hd_backend.service.UserService;
import com.hd.hd_backend.utils.JsonUtils;
import com.hd.hd_backend.utils.WebSocketCode;
import com.hd.hd_backend.utils.WebSocketSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class UserWebsocketHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private UserWebSocketHandler handler;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserMapper userMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        WebSocketSessionManager.removeAllSessions();

        // Setup session attributes
        Map<String, Object> attributes = new HashMap<>();
        when(session.getAttributes()).thenReturn(attributes);
    }

    // Helper method to create a test user
    private NormalUser createTestUser() {
        NormalUser user = new NormalUser();
        user.setId(1);
        user.setName("Test User");
        user.setPassword("password");
        user.setAge(30);
        user.setWeight(70.0);
        user.setHeight(175);
        user.setGender(1);
        user.setActivityFactor(1.5);
        user.setPhone("12345678900");
        return user;
    }

    @Test
    void testHandleRegisterSuccess() throws Exception {
        // Arrange
        NormalUser testUser = createTestUser();
        when(userService.register(any(NormalUser.class))).thenReturn(testUser);

        String registerMessage = "register:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(registerMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        verify(userService).register(any(NormalUser.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.REGISTER_SUCCESS.ordinal())) &&
                    payload.contains("Test User");
        }));

        // Verify session was added to manager
        assertNotNull(WebSocketSessionManager.getSession(testUser.getId()));
    }

//    @Test
//    void testHandleRegisterFailure() throws Exception {
//        // Arrange
//        NormalUser testUser = createTestUser();
//        when(userService.register(any(NormalUser.class))).thenThrow(new RuntimeException("Registration failed"));
//
//        String registerMessage = "register:" + JsonUtils.toJson(testUser);
//        TextMessage message = new TextMessage(registerMessage);
//
//        // Act
//        handler.handleTextMessage(session, message);
//
//        // Assert
//        verify(userService).register(any(NormalUser.class));
//        verify(session).sendMessage(argThat(textMessage -> {
//            String payload = (String) textMessage.getPayload();
//            return payload.contains(String.valueOf(WebSocketCode.REGISTER_FAIL.ordinal())) &&
//                    payload.contains("Registration failed");
//        }));
//
//        // Verify session was NOT added to manager
//        assertNull(WebSocketSessionManager.getSession(testUser.getId()));
//    }
    @Test
    void testHandleRegisterFailure() throws Exception {
        NormalUser testUser = createTestUser();

        // 模拟注册失败：比如手机号已存在，服务抛出异常
        when(userService.register(any(NormalUser.class)))
                .thenThrow(new Exception("手机号已存在"));

        String registerMessage = "register:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(registerMessage);

        handler.handleTextMessage(session, message);

        verify(userService).register(any(NormalUser.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.REGISTER_FAIL.ordinal())) &&
                    payload.contains("手机号已存在");
        }));

        // 因注册失败，不应加入 session manager
        assertNull(WebSocketSessionManager.getSession(testUser.getId()));
    }


    @Test
    void testHandleLoginSuccess() throws Exception {
        // Arrange
        NormalUser testUser = createTestUser();
        when(userService.login(any(User.class))).thenReturn(testUser);

        String loginMessage = "login:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(loginMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        verify(userService).login(any(User.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.LOGIN_SUCCESS.ordinal())) &&
                    payload.contains("Test User");
        }));

        // Verify session was added to manager
        assertNotNull(WebSocketSessionManager.getSession(testUser.getId()));
        assertEquals(testUser.getId(), session.getAttributes().get("userId"));
    }

    @Test
    void testHandleLoginFailure() throws Exception {
        // Arrange
        User testUser = createTestUser();
        when(userService.login(any(User.class))).thenThrow(new RuntimeException("Invalid credentials"));

        String loginMessage = "login:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(loginMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        verify(userService).login(any(User.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.LOGIN_FAIL.ordinal())) &&
                    payload.contains("Invalid credentials");
        }));

        // Verify session was NOT added to manager
        assertNull(WebSocketSessionManager.getSession(testUser.getId()));
    }
    @Test
    public void testLoginSuccess() throws Exception {
        User input = new User();
        input.setPhone("12345678901");
        input.setPassword("123456");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setPhone("12345678901");
        mockUser.setPassword("123456");
        mockUser.setIsAdmin(0);

        when(userMapper.findByPhone("12345678901")).thenReturn(mockUser);
        when(userMapper.findById(1)).thenReturn(new NormalUser());  // 返回普通用户对象

        User result = userService.login(input);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testLoginFail_PhoneEmpty() {
        User input = new User();
        input.setPhone("");
        input.setPassword("123456");

        Exception exception = assertThrows(Exception.class, () -> userService.login(input));
        assertEquals("手机号不能为空", exception.getMessage());
    }

    @Test
    public void testLoginFail_PasswordEmpty() {
        User input = new User();
        input.setPhone("12345678901");
        input.setPassword("");

        Exception exception = assertThrows(Exception.class, () -> userService.login(input));
        assertEquals("密码不能为空", exception.getMessage());
    }

    @Test
    public void testLoginFail_UserNotExist() {
        User input = new User();
        input.setPhone("12345678901");
        input.setPassword("123456");

        when(userMapper.findByPhone("12345678901")).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> userService.login(input));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    public void testLoginFail_WrongPassword() {
        User input = new User();
        input.setPhone("12345678901");
        input.setPassword("wrongpass");

        User mockUser = new User();
        mockUser.setPhone("12345678901");
        mockUser.setPassword("correctpass");

        when(userMapper.findByPhone("12345678901")).thenReturn(mockUser);

        Exception exception = assertThrows(Exception.class, () -> userService.login(input));
        assertEquals("密码错误", exception.getMessage());
    }
    @Test
    void testHandleUpdateUserSuccess() throws Exception {
        // Arrange
        NormalUser testUser = createTestUser();
        NormalUser updatedUser = createTestUser();
        updatedUser.setWeight(75.0);

        doNothing().when(userService).updateUser(anyInt(), any(NormalUser.class));
        when(userService.getUserById(anyInt())).thenReturn(updatedUser);

        // Set userId in session attributes to simulate logged-in user
        session.getAttributes().put("userId", testUser.getId());

        String updateMessage = "updateUser:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(updateMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        verify(userService).updateUser(eq(testUser.getId()), any(NormalUser.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.UPDATE_USER_SUCCESS.ordinal())) &&
                    payload.contains("75.0");
        }));
    }

    @Test
    void testHandleUpdateUserWithoutLogin() throws Exception {
        // Arrange
        NormalUser testUser = createTestUser();

        // Don't set userId in session attributes to simulate not logged in

        String updateMessage = "updateUser:" + JsonUtils.toJson(testUser);
        TextMessage message = new TextMessage(updateMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        verify(userService, never()).updateUser(anyInt(), any(NormalUser.class));
        verify(session).sendMessage(argThat(textMessage -> {
            String payload = (String) textMessage.getPayload();
            return payload.contains(String.valueOf(WebSocketCode.UPDATE_USER_FAIL.ordinal())) &&
                    payload.contains("用户未登录");
        }));
    }

    @Test
    void testHandleUnknownAction() throws Exception {
        // Arrange
        String unknownMessage = "unknownAction:someData";
        TextMessage message = new TextMessage(unknownMessage);

        // Act
        handler.handleTextMessage(session, message);

        // Assert
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        System.out.println("[TEST] 实际发送的消息: " + captor.getValue().getPayload());


    }
}