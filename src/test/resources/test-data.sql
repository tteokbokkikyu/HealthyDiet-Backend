
-- 插入基础用户数据(注意自增ID从20开始)
INSERT INTO user (id, name, password, profile_picture, is_admin, phone)
VALUES
    (20, 'testUser', 'password123', 'profile1.jpg', 0, '13800138000'),
    (21, 'testAdmin', '$2a$10$xVCH4IAHwXo6GZL6gCQG8e8QzBQ3DfjZzB7Hd9LzL9Jk5Jt6YbWXe', 'profile2.jpg', 1, '13912345678'),
    (22, 'blockedUser', '$2a$10$xVCH4IAHwXo6GZL6gCQG8e8QzBQ3DfjZzB7Hd9LzL9Jk5Jt6YbWXe', 'profile3.jpg', 0, '13712345678');

-- 插入普通用户数据
INSERT INTO normal_user (user_id, weight, age, height, is_blocked, gender, activity_factor)
VALUES
    (20, 70.0, 30, 175, 0, 1, 1.2),
    (22, 65.0, 25, 165, 1, 0, 1.5);