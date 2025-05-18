package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.entity.NormalUser;
import com.hd.hd_backend.entity.User;
import com.hd.hd_backend.entity.Weight;
import com.hd.hd_backend.mapper.UserMapper;
import com.hd.hd_backend.mapper.WeightMapper;
import com.hd.hd_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeightMapper weightMapper;
    @Override
    public NormalUser register(NormalUser normalUser) throws Exception {

        // 保存到数据库
        userMapper.insertUser(normalUser);
        int id=userMapper.findByPhone(normalUser.getPhone()).getId();
        normalUser.setId(id);
        userMapper.insertNormalUser(normalUser);

        // 创建初始体重记录
        if (normalUser.getWeight() != null) {  // 只有当用户提供了初始体重时才创建记录
            Weight weight = new Weight();
            weight.setUserId(id);
            weight.setWeight(normalUser.getWeight());  // 使用注册时提供的体重
            weightMapper.insertWeight(weight);
        }

        return userMapper.findById(id);
    }

    @Override
    public User login(User  normalUser) throws Exception {
        // 验证参数
        if (normalUser.getPhone() == null || normalUser.getPhone().trim().isEmpty()) {
            throw new Exception("手机号不能为空");
        }
        if (normalUser.getPassword() == null || normalUser.getPassword().trim().isEmpty()) {
            throw new Exception("密码不能为空");
        }

        // 通过手机号查找用户
        User user = userMapper.findByPhone(normalUser.getPhone());
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 验证密码
        if (!user.getPassword().equals(normalUser.getPassword())) {
            throw new Exception("密码错误");
        }

        if (user.getIsAdmin() == 0)
        {
            NormalUser normal=userMapper.findById(user.getId());
            return normal;
        }
        else
        {
            return user;
        }
    }

    @Override
    public void updateUser(Integer userId, NormalUser updateInfo) throws Exception {
        NormalUser user = userMapper.findById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 如果要更新用户名，需要检查新用户名是否已存在
//        if (updateInfo.getName()!=null&&!(updateInfo.getName().equals(user.getName()))) {
//            User existingUser = userMapper.findByPhone(updateInfo.getName());
//            if (existingUser != null && !existingUser.getId().equals(userId)) {
//                throw new Exception("用户名已存在");
//            }
//        }

        // 如果要更新手机号，需要检查新手机号是否已存在
        if (updateInfo.getPhone() != null && !updateInfo.getPhone().isEmpty()) {
            User existingUser = userMapper.findByPhone(updateInfo.getPhone());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new Exception("手机号已被使用");
            }
        }

        user.update(updateInfo);
        userMapper.update(user);
    }

    @Override
    public NormalUser getUserById(Integer userId) throws Exception {
        NormalUser user = userMapper.findById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        return user;
    }


}