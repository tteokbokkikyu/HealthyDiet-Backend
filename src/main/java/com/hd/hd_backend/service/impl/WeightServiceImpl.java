package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.entity.Weight;
import com.hd.hd_backend.mapper.WeightMapper;
import com.hd.hd_backend.service.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeightServiceImpl implements WeightService {
    @Autowired
    private WeightMapper weightMapper;

    @Override
    public void addWeight(Weight weight) {
        try {
            weightMapper.insertWeight(weight);
        } catch (Exception e) {

            throw new RuntimeException("添加体重失败", e);
        }
    }

    @Override
    public void deleteWeight(Integer userId, String time) {
        try {
            weightMapper.deleteWeight(userId, time);
        } catch (Exception e) {

            throw new RuntimeException("删除体重失败", e);
        }
    }

    @Override
    public void updateWeight(Weight weight) {
        try {
            weightMapper.updateWeight(weight);
        } catch (Exception e) {

            throw new RuntimeException("更新体重失败", e);
        }
    }

    @Override
    public List<Weight> getUserWeights(Integer userId) {
        try {
            return weightMapper.getUserWeights(userId);
        } catch (Exception e) {

            throw new RuntimeException("获取用户体重失败", e);
        }
    }

    @Override
    public Weight getLatestWeight(Integer userId) {
        try {
            return weightMapper.getLatestWeight(userId);
        } catch (Exception e) {

            throw new RuntimeException("获取最新体重失败", e);
        }
    }
}
