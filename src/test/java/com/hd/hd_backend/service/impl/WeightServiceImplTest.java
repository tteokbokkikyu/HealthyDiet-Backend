package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.entity.Weight;
import com.hd.hd_backend.mapper.WeightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeightServiceImplTest {

    @Mock
    private WeightMapper weightMapper;

    @InjectMocks
    private WeightServiceImpl weightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addWeight_Success() {
        // 准备测试数据
        Weight weight = new Weight();
        weight.setUserId(1);
        weight.setWeight(70.5);
        weight.setTime("2025-05-13");

        // 执行测试
        assertDoesNotThrow(() -> weightService.addWeight(weight));

        // 验证调用
        verify(weightMapper, times(1)).insertWeight(weight);
    }

    @Test
    void addWeight_ThrowsException() {
        // 准备测试数据
        Weight weight = new Weight();
        doThrow(new RuntimeException("Database error"))
                .when(weightMapper).insertWeight(weight);

        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class,
                () -> weightService.addWeight(weight));
        assertEquals("添加体重失败", exception.getMessage());
    }

    @Test
    void deleteWeight_Success() {
        // 准备测试数据
        Integer userId = 1;
        String time = "2025-05-13";

        // 执行测试
        assertDoesNotThrow(() -> weightService.deleteWeight(userId, time));

        // 验证调用
        verify(weightMapper, times(1)).deleteWeight(userId, time);
    }

    @Test
    void deleteWeight_ThrowsException() {
        // 准备测试数据
        Integer userId = 1;
        String time = "2025-05-13";
        doThrow(new RuntimeException("Database error"))
                .when(weightMapper).deleteWeight(userId, time);

        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class,
                () -> weightService.deleteWeight(userId, time));
        assertEquals("删除体重失败", exception.getMessage());
    }

    @Test
    void updateWeight_Success() {
        // 准备测试数据
        Weight weight = new Weight();
        weight.setUserId(1);
        weight.setWeight(75.0);
        weight.setTime("2025-05-13");

        // 执行测试
        assertDoesNotThrow(() -> weightService.updateWeight(weight));

        // 验证调用
        verify(weightMapper, times(1)).updateWeight(weight);
    }

    @Test
    void updateWeight_ThrowsException() {
        // 准备测试数据
        Weight weight = new Weight();
        doThrow(new RuntimeException("Database error"))
                .when(weightMapper).updateWeight(weight);

        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class,
                () -> weightService.updateWeight(weight));
        assertEquals("更新体重失败", exception.getMessage());
    }

    @Test
    void getUserWeights_Success() {
        // 准备测试数据
        Integer userId = 1;
        List<Weight> expectedWeights = Arrays.asList(
                new Weight(1, 70.5, "2025-05-13"),
                new Weight(1, 71.0, "2025-05-14")
        );
        when(weightMapper.getUserWeights(userId)).thenReturn(expectedWeights);

        // 执行测试
        List<Weight> actualWeights = weightService.getUserWeights(userId);

        // 验证结果
        assertNotNull(actualWeights);
        assertEquals(expectedWeights.size(), actualWeights.size());
        assertEquals(expectedWeights, actualWeights);
    }

    @Test
    void getUserWeights_ThrowsException() {
        // 准备测试数据
        Integer userId = 1;
        when(weightMapper.getUserWeights(userId))
                .thenThrow(new RuntimeException("Database error"));

        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class,
                () -> weightService.getUserWeights(userId));
        assertEquals("获取用户体重失败", exception.getMessage());
    }

    @Test
    void getLatestWeight_Success() {
        // 准备测试数据
        Integer userId = 1;
        Weight expectedWeight = new Weight(1, 71.0, "2024-05-14");
        when(weightMapper.getLatestWeight(userId)).thenReturn(expectedWeight);

        // 执行测试
        Weight actualWeight = weightService.getLatestWeight(userId);

        // 验证结果
        assertNotNull(actualWeight);
        assertEquals(expectedWeight, actualWeight);
    }

    @Test
    void getLatestWeight_ThrowsException() {
        // 准备测试数据
        Integer userId = 1;
        when(weightMapper.getLatestWeight(userId))
                .thenThrow(new RuntimeException("Database error"));

        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class,
                () -> weightService.getLatestWeight(userId));
        assertEquals("获取最新体重失败", exception.getMessage());
    }
}