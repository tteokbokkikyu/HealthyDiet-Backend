package com.hd.hd_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NormalUserTest {

    private NormalUser normalUser;
    private NormalUser updateInfo;

    @BeforeEach
    void setUp() {
        normalUser = new NormalUser();
        updateInfo = new NormalUser();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(normalUser, "NormalUser对象应该被成功创建");
        assertEquals("https://img1.baidu.com/it/u=534429813,2995452219&fm=253&fmt=auto?w=800&h=800",
                normalUser.getProfilePicture(), "默认头像URL应该被正确设置");
        assertEquals(0, normalUser.getIsBlocked(), "默认isBlocked应该为0");
        assertEquals(0, normalUser.getIsAdmin(), "默认isAdmin应该为0");
    }

    @Test
    void testWeightGetterAndSetter() {
        // 测试正常值
        normalUser.setWeight(65.5);
        assertEquals(65.5, normalUser.getWeight(), 0.001, "体重应该被正确设置和获取");

        // 测试边界值
        normalUser.setWeight(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, normalUser.getWeight(), "应该能处理最大双精度值");

        normalUser.setWeight(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, normalUser.getWeight(), "应该能处理最小双精度值");

        // 测试null值
        normalUser.setWeight(null);
        assertNull(normalUser.getWeight(), "应该能处理null值");
    }

    @Test
    void testAgeGetterAndSetter() {
        // 测试正常值
        normalUser.setAge(25);
        assertEquals(25, normalUser.getAge(), "年龄应该被正确设置和获取");

        // 测试边界值
        normalUser.setAge(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, normalUser.getAge(), "应该能处理最大整数值");

        normalUser.setAge(0);
        assertEquals(0, normalUser.getAge(), "应该能处理0值");

        // 测试null值
        normalUser.setAge(null);
        assertNull(normalUser.getAge(), "应该能处理null值");
    }

    @Test
    void testHeightGetterAndSetter() {
        // 测试正常值
        normalUser.setHeight(175);
        assertEquals(175, normalUser.getHeight(), "身高应该被正确设置和获取");

        // 测试边界值
        normalUser.setHeight(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, normalUser.getHeight(), "应该能处理最大整数值");

        normalUser.setHeight(0);
        assertEquals(0, normalUser.getHeight(), "应该能处理0值");

        // 测试null值
        normalUser.setHeight(null);
        assertNull(normalUser.getHeight(), "应该能处理null值");
    }

    @Test
    void testGenderGetterAndSetter() {
        // 测试正常值
        normalUser.setGender(1);
        assertEquals(1, normalUser.getGender(), "性别应该被正确设置和获取");

        // 测试其他可能值
        normalUser.setGender(0);
        assertEquals(0, normalUser.getGender(), "应该能处理0值");

        normalUser.setGender(2);
        assertEquals(2, normalUser.getGender(), "应该能处理2值");

        // 测试null值
        normalUser.setGender(null);
        assertNull(normalUser.getGender(), "应该能处理null值");
    }

    @Test
    void testActivityFactorGetterAndSetter() {
        // 测试正常值
        normalUser.setActivityFactor(1.5);
        assertEquals(1.5, normalUser.getActivityFactor(), 0.001, "活动系数应该被正确设置和获取");

        // 测试边界值
        normalUser.setActivityFactor(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, normalUser.getActivityFactor(), "应该能处理最大双精度值");

        normalUser.setActivityFactor(0.0);
        assertEquals(0.0, normalUser.getActivityFactor(), "应该能处理0值");

        // 测试null值
        normalUser.setActivityFactor(null);
        assertNull(normalUser.getActivityFactor(), "应该能处理null值");
    }

    @Test
    void testIsBlockedGetterAndSetter() {
        // 测试正常值
        normalUser.setIsBlocked(1);
        assertEquals(1, normalUser.getIsBlocked(), "isBlocked应该被正确设置和获取");

        // 测试其他可能值
        normalUser.setIsBlocked(0);
        assertEquals(0, normalUser.getIsBlocked(), "应该能处理0值");

        // 测试边界值
        normalUser.setIsBlocked(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, normalUser.getIsBlocked(), "应该能处理最大整数值");

        // 测试null值
        normalUser.setIsBlocked(null);
        assertNull(normalUser.getIsBlocked(), "应该能处理null值");
    }

    @Test
    void testUpdateMethod() {
        // 初始化原始用户
        normalUser.setName("张三");
        normalUser.setPassword("oldPassword");
        normalUser.setProfilePicture("old.jpg");
        normalUser.setWeight(70.0);
        normalUser.setAge(30);
        normalUser.setHeight(175);
        normalUser.setPhone("13800138000");
        normalUser.setGender(1);
        normalUser.setActivityFactor(1.2);

        // 准备更新信息
        updateInfo.setName("李四");
        updateInfo.setPassword("newPassword");
        updateInfo.setProfilePicture("new.jpg");
        updateInfo.setWeight(65.0);
        updateInfo.setAge(31);
        updateInfo.setHeight(176);
        updateInfo.setPhone("13912345678");
        updateInfo.setGender(0);
        updateInfo.setActivityFactor(1.5);

        // 执行更新
        normalUser.update(updateInfo);

        // 验证所有字段是否被正确更新
        assertEquals("李四", normalUser.getName(), "姓名应该被更新");
        assertEquals("newPassword", normalUser.getPassword(), "密码应该被更新");
        assertEquals("new.jpg", normalUser.getProfilePicture(), "头像URL应该被更新");
        assertEquals(65.0, normalUser.getWeight(), 0.001, "体重应该被更新");
        assertEquals(31, normalUser.getAge(), "年龄应该被更新");
        assertEquals(176, normalUser.getHeight(), "身高应该被更新");
        assertEquals("13912345678", normalUser.getPhone(), "电话号码应该被更新");
        assertEquals(0, normalUser.getGender(), "性别应该被更新");
        assertEquals(1.5, normalUser.getActivityFactor(), 0.001, "活动系数应该被更新");
    }

    @Test
    void testPartialUpdate() {
        // 初始化原始用户
        normalUser.setName("张三");
        normalUser.setPassword("oldPassword");
        normalUser.setWeight(70.0);
        normalUser.setAge(30);

        // 准备部分更新信息
        NormalUser partialUpdate = new NormalUser();
        partialUpdate.setName("李四");
        partialUpdate.setAge(31);

        // 执行更新
        normalUser.update(partialUpdate);

        // 验证
        assertEquals("李四", normalUser.getName(), "姓名应该被更新");
        assertEquals(31, normalUser.getAge(), "年龄应该被更新");
        assertEquals("oldPassword", normalUser.getPassword(), "密码不应被更新");
        assertEquals(70.0, normalUser.getWeight(), 0.001, "体重不应被更新");
    }

    @Test
    void testUpdateWithNullValues() {
        // 初始化原始用户
        normalUser.setName("张三");
        normalUser.setPassword("password");
        normalUser.setWeight(70.0);

        // 准备更新信息(包含null值)
        NormalUser nullUpdate = new NormalUser();
        nullUpdate.setName(null);
        nullUpdate.setPassword("");
        nullUpdate.setWeight(null);

        // 执行更新
        normalUser.update(nullUpdate);

        // 验证
        assertEquals("张三", normalUser.getName(), "姓名不应被null值更新");
        assertEquals("password", normalUser.getPassword(), "密码不应被空字符串更新");
        assertEquals(70.0, normalUser.getWeight(), 0.001, "体重不应被null值更新");
    }

    @Test
    void testInheritedFields() {
        // 测试继承自User类的字段
        normalUser.setId(100);
        normalUser.setIsAdmin(0);
        normalUser.setPhone("13800138000");

        assertEquals(100, normalUser.getId(), "继承的ID字段应该正常工作");
        assertEquals(0, normalUser.getIsAdmin(), "继承的isAdmin字段应该正常工作");
        assertEquals("13800138000", normalUser.getPhone(), "继承的phone字段应该正常工作");
    }
}