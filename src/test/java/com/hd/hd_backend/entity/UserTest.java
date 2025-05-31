package com.hd.hd_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(user, "User对象应该被成功创建");
    }

    @Test
    void testIdGetterAndSetter() {
        // 测试正常值
        user.setId(1);
        assertEquals(1, user.getId(), "ID应该被正确设置和获取");

        // 测试边界值
        user.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, user.getId(), "应该能处理最大整数值");

        user.setId(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, user.getId(), "应该能处理最小整数值");
    }

    @Test
    void testNameGetterAndSetter() {
        // 测试正常字符串
        user.setName("张三");
        assertEquals("张三", user.getName(), "姓名应该被正确设置和获取");

        // 测试空值
        user.setName(null);
        assertNull(user.getName(), "应该能处理null值");

        // 测试空字符串
        user.setName("");
        assertEquals("", user.getName(), "应该能处理空字符串");

        // 测试长字符串
        String longName = "这是一个非常长的名字，用来测试字符串长度限制";
        user.setName(longName);
        assertEquals(longName, user.getName(), "应该能处理长字符串");
    }

    @Test
    void testPasswordGetterAndSetter() {
        // 测试正常密码
        user.setPassword("securePassword123");
        assertEquals("securePassword123", user.getPassword(), "密码应该被正确设置和获取");

        // 测试特殊字符
        user.setPassword("p@ssw0rd!@#");
        assertEquals("p@ssw0rd!@#", user.getPassword(), "应该能处理特殊字符");

        // 测试空值
        user.setPassword(null);
        assertNull(user.getPassword(), "应该能处理null值");
    }

    @Test
    void testProfilePictureGetterAndSetter() {
        // 测试正常URL
        user.setProfilePicture("http://example.com/profile.jpg");
        assertEquals("http://example.com/profile.jpg", user.getProfilePicture(), "头像URL应该被正确设置和获取");

        // 测试空值
        user.setProfilePicture(null);
        assertNull(user.getProfilePicture(), "应该能处理null值");

        // 测试相对路径
        user.setProfilePicture("/images/profile.png");
        assertEquals("/images/profile.png", user.getProfilePicture(), "应该能处理相对路径");
    }

    @Test
    void testIsAdminGetterAndSetter() {
        // 测试管理员状态
        user.setIsAdmin(1);
        assertEquals(1, user.getIsAdmin(), "管理员状态应该被正确设置和获取");

        // 测试非管理员状态
        user.setIsAdmin(0);
        assertEquals(0, user.getIsAdmin(), "非管理员状态应该被正确设置和获取");

        // 测试边界值
        user.setIsAdmin(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, user.getIsAdmin(), "应该能处理最大整数值");
    }

    @Test
    void testPhoneGetterAndSetter() {
        // 测试正常电话号码
        user.setPhone("13800138000");
        assertEquals("13800138000", user.getPhone(), "电话号码应该被正确设置和获取");

        // 测试国际号码
        user.setPhone("+8613800138000");
        assertEquals("+8613800138000", user.getPhone(), "应该能处理国际号码");

        // 测试带分隔符的号码
        user.setPhone("138-0013-8000");
        assertEquals("138-0013-8000", user.getPhone(), "应该能处理带分隔符的号码");

        // 测试空值
        user.setPhone(null);
        assertNull(user.getPhone(), "应该能处理null值");
    }

    @Test
    void testAllPropertiesTogether() {
        // 测试同时设置所有属性
        user.setId(100);
        user.setName("李四");
        user.setPassword("mypassword");
        user.setProfilePicture("/profiles/100.jpg");
        user.setIsAdmin(0);
        user.setPhone("13912345678");

        assertEquals(100, user.getId(), "ID应该保持设置的值");
        assertEquals("李四", user.getName(), "姓名应该保持设置的值");
        assertEquals("mypassword", user.getPassword(), "密码应该保持设置的值");
        assertEquals("/profiles/100.jpg", user.getProfilePicture(), "头像URL应该保持设置的值");
        assertEquals(0, user.getIsAdmin(), "管理员状态应该保持设置的值");
        assertEquals("13912345678", user.getPhone(), "电话号码应该保持设置的值");
    }
}