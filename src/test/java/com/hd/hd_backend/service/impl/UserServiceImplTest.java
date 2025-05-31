package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;
import com.hd.hd_backend.entity.Weight;
import com.hd.hd_backend.mapper.UserMapper;
import com.hd.hd_backend.mapper.WeightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private WeightMapper weightMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private NormalUser testNormalUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setId(31);
        testUser.setName("testUser");
        testUser.setPassword("password123");
        testUser.setPhone("13800138000");
        testUser.setIsAdmin(0);

        testNormalUser = new NormalUser();
        testNormalUser.setName("testUser");
        testNormalUser.setPassword("password123");
        testNormalUser.setPhone("13800138000");
        testNormalUser.setWeight(70.0);
        testNormalUser.setHeight(175);
        testNormalUser.setAge(30);
        testNormalUser.setId(3);



    }

    // region 注册功能测试
    @Test
    void register_ShouldSuccess_WhenAllFieldsValid() throws Exception {
        // 模拟Mapper行为
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.insertUser(any(User.class))).thenReturn(1);
        when(userMapper.findByPhone(testNormalUser.getPhone())).thenReturn(testUser);

        when(userMapper.findById(anyInt())).thenReturn(testNormalUser);

        // 执行测试
        NormalUser result = userService.register(testNormalUser);

        // 验证结果
        assertNotNull(result);
        assertEquals("testUser", result.getName());
        assertEquals("13800138000", result.getPhone());

        // 验证交互
        verify(userMapper, times(1)).insertUser(any(NormalUser.class));
        verify(userMapper, times(1)).insertNormalUser(any(NormalUser.class));
        verify(weightMapper, times(1)).insertWeight(any(Weight.class));
    }

    @Test
    void register_ShouldSuccess_WhenWeightNotProvided() throws Exception {
        // 准备无体重的测试用户
        testNormalUser.setWeight(null);

        // 模拟Mapper行为
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.insertUser(any(User.class))).thenReturn(30);
        when(userMapper.findByPhone(testNormalUser.getPhone())).thenReturn(testUser);

        when(userMapper.findById(anyInt())).thenReturn(testNormalUser);

        // 执行测试
        NormalUser result = userService.register(testNormalUser);

        // 验证结果
        assertNotNull(result);

        // 验证没有调用体重插入
        verify(weightMapper, never()).insertWeight(any(Weight.class));
    }


    // region 登录功能测试
    @Test
    void login_ShouldSuccess_WhenCredentialsCorrect() throws Exception {
        // 模拟普通用户
        testUser.setIsAdmin(0);
        when(userMapper.findByPhone(testUser.getPhone())).thenReturn(testUser);
        when(userMapper.findById(testUser.getId())).thenReturn(testNormalUser);

        // 执行测试
        User result = userService.login(testUser);

        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof NormalUser);
        assertEquals("testUser", result.getName());
    }

    @Test
    void login_ShouldSuccess_WhenAdminUser() throws Exception {
        // 模拟管理员用户
        testUser.setIsAdmin(1);
        when(userMapper.findByPhone(testUser.getPhone())).thenReturn(testUser);

        // 执行测试
        User result = userService.login(testUser);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getIsAdmin());
    }

    @Test
    void login_ShouldThrowException_WhenPhoneEmpty() {
        testUser.setPhone(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(testUser);
        });

        assertEquals("手机号不能为空", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordEmpty() {
        testUser.setPassword(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(testUser);
        });

        assertEquals("密码不能为空", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(userMapper.findByPhone(testUser.getPhone())).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(testUser);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIncorrect() {

        when(userMapper.findByPhone(testUser.getPhone())).thenReturn(testUser);
        testUser.setPassword("wrongPassword");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(testNormalUser);
        });

        assertEquals("密码错误", exception.getMessage());
    }

    // endregion

    // region 更新用户测试
    @Test
    void updateUser_ShouldSuccess_WhenAllFieldsValid() throws Exception {
        // 模拟现有用户
        NormalUser existingUser = new NormalUser();
        existingUser.setId(1);
        existingUser.setPhone("13800138000");
        when(userMapper.findById(1)).thenReturn(existingUser);
        when(userMapper.findByPhone("13912345678")).thenReturn(null);

        // 准备更新数据
        NormalUser updateInfo = new NormalUser();
        updateInfo.setPhone("13912345678");
        updateInfo.setName("newName");
        updateInfo.setWeight(75.0);

        // 执行测试
        userService.updateUser(1, updateInfo);

        // 验证交互
        verify(userMapper, times(1)).update(any(NormalUser.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userMapper.findById(1)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateUser(1, new NormalUser());
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void updateUser_ShouldThrowException_WhenPhoneAlreadyUsed() {
        // 模拟现有用户
        NormalUser existingUser = new NormalUser();
        existingUser.setId(1);
        existingUser.setPhone("13800138000");
        when(userMapper.findById(1)).thenReturn(existingUser);

        // 模拟手机号已被其他用户使用
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setPhone("13912345678");
        when(userMapper.findByPhone("13912345678")).thenReturn(otherUser);

        // 准备更新数据
        NormalUser updateInfo = new NormalUser();
        updateInfo.setPhone("13912345678");

        // 验证异常
        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateUser(1, updateInfo);
        });

        assertEquals("手机号已被使用", exception.getMessage());
    }
    // endregion

    // region 获取用户测试
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        when(userMapper.findById(1)).thenReturn(testNormalUser);

        NormalUser result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testUser", result.getName());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotExists() {
        when(userMapper.findById(1)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserById(1);
        });

        assertEquals("用户不存在", exception.getMessage());
    }
    // endregion
}