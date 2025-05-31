package com.hd.hd_backend.mapper;

import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    // 测试ID常量
    private static final int EXISTING_USER_ID = 20;
    private static final int ADMIN_USER_ID = 21;
    private static final int BLOCKED_USER_ID = 22;
    private static final int NON_EXISTENT_USER_ID = 99;

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByName_ShouldReturnUser_WhenNameExists() {
        User user = userMapper.findByName("testUser");

        assertNotNull(user);
        assertEquals(EXISTING_USER_ID, user.getId());
        assertEquals("13800138000", user.getPhone());
        assertEquals(0, user.getIsAdmin());
    }

    @Test
    @Sql(scripts = "/test-schema.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByName_ShouldReturnNull_WhenNameNotExists() {
        assertNull(userMapper.findByName("nonExistingUser"));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByPhone_ShouldReturnUser_WhenPhoneExists() {
        User user = userMapper.findByPhone("13800138000");

        assertNotNull(user);
        assertEquals(EXISTING_USER_ID, user.getId());
        assertEquals("testUser", user.getName());
    }

    @Test
    @Sql(scripts = "/test-schema.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByPhone_ShouldReturnNull_WhenPhoneNotExists() {
        assertNull(userMapper.findByPhone("12345678900"));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ShouldReturnNormalUser_WhenIdExists() {
        NormalUser user = userMapper.findById(EXISTING_USER_ID);

        assertNotNull(user);
        assertEquals(EXISTING_USER_ID, user.getId());
        assertEquals(70.0, user.getWeight(), 0.001);
        assertEquals(175, user.getHeight());
        assertEquals(0, user.getIsBlocked());
    }

    @Test
    @Sql(scripts = "/test-schema.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ShouldReturnNull_WhenIdNotExists() {
        assertNull(userMapper.findById(NON_EXISTENT_USER_ID));
    }

    @Test
    @Sql(scripts = "/test-schema.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void insertUser_ShouldInsertUserSuccessfully() {
        User newUser = new User();
        newUser.setName("newUser");
        newUser.setIsAdmin(1);
        newUser.setPassword("encryptedPassword");
        newUser.setPhone("13112345678");

        int result = userMapper.insertUser(newUser);
        assertEquals(1, result);

        User insertedUser = userMapper.findByPhone("13112345678");
        assertNotNull(insertedUser);
        assertEquals("newUser", insertedUser.getName());
        assertTrue(insertedUser.getId() >= 20, "ID应该从20开始自增");
    }

    @Test
    @Sql(scripts = "/test-schema.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void insertNormalUser_ShouldInsertNormalUserSuccessfully() {
        // 先插入基础用户
        User baseUser = new User();
        baseUser.setName("newNormalUser");
        baseUser.setPassword("encryptedPassword");
        baseUser.setPhone("13212345678");
        baseUser.setIsAdmin(1);
        userMapper.insertUser(baseUser);

        // 插入普通用户信息
        NormalUser normalUser = new NormalUser();
        normalUser.setId(baseUser.getId());
        normalUser.setWeight(65.5);
        normalUser.setHeight(170);
        normalUser.setAge(25);

        userMapper.insertNormalUser(normalUser);

        NormalUser insertedUser = userMapper.findById(baseUser.getId());
        assertNotNull(insertedUser);
        assertEquals(65.5, insertedUser.getWeight(), 0.001);
        assertEquals(170, insertedUser.getHeight());
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void blockById_ShouldBlockUserSuccessfully() {
        userMapper.blockById(EXISTING_USER_ID);

        NormalUser user = userMapper.findById(EXISTING_USER_ID);
        assertEquals(1, user.getIsBlocked());
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void unblockById_ShouldUnblockUserSuccessfully() {
        userMapper.unblockById(BLOCKED_USER_ID);

        NormalUser user = userMapper.findById(BLOCKED_USER_ID);
        assertEquals(0, user.getIsBlocked());
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ShouldUpdateNormalUserInfoSuccessfully() {

        NormalUser user = userMapper.findById(EXISTING_USER_ID);
        user.setWeight(68.0);
        user.setActivityFactor(1.8);
        user.setHeight(176);


        userMapper.update(user);

        NormalUser updatedUser = userMapper.findById(EXISTING_USER_ID);
        assertEquals(68.0, updatedUser.getWeight(), 0.001);
        assertEquals(176, updatedUser.getHeight());
        assertEquals(1.2, updatedUser.getActivityFactor(), 0.001);
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllNormalUsers_ShouldReturnOnlyNormalUsers() {
        List<NormalUser> users = userMapper.findAllNormalUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId() == EXISTING_USER_ID));
        assertTrue(users.stream().anyMatch(u -> u.getId() == BLOCKED_USER_ID));
        assertFalse(users.stream().anyMatch(u -> u.getId() == ADMIN_USER_ID),
                "不应该返回管理员用户");
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void phoneNumberUniqueConstraint_ShouldBeEnforced() {
        User duplicatePhoneUser = new User();
        duplicatePhoneUser.setName("duplicateUser");
        duplicatePhoneUser.setPassword("password");
        duplicatePhoneUser.setPhone("13800138000"); // 已存在的手机号
        duplicatePhoneUser.setIsAdmin(0);

        try {
            userMapper.insertUser(duplicatePhoneUser);
            fail("应该抛出异常，因为手机号已存在");
        } catch (Exception e) {
            // 期望抛出异常
            assertTrue(e.getMessage().contains("Duplicate entry") ||
                    e.getMessage().contains("唯一键约束"));
        }
    }
}