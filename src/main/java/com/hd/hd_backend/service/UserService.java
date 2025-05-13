package com.hd.hd_backend.service;

import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;

public interface UserService {

// 注册普通用户
    NormalUser register(NormalUser normalUser) throws Exception;
// 用户登录方法，参数为普通用户对象，返回值为用户对象，抛出异常
    User login(User normalUser) throws Exception;
// 更新用户信息
    void updateUser(Integer userId, NormalUser updateInfo) throws Exception;
// 根据用户ID获取用户信息
    NormalUser getUserById(Integer userId) throws Exception;
}