package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.dto.FoodRecordDTO;
import com.hd.hd_backend.entity.FoodItem;
import com.hd.hd_backend.entity.FoodRecord;
import com.hd.hd_backend.mapper.FoodMapper;
import com.hd.hd_backend.mapper.FoodRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceImplTest {

    @Mock
    private FoodMapper foodMapper;

    @Mock
    private FoodRecordMapper foodRecordMapper;

    @InjectMocks
    private FoodServiceImpl foodService;

    private FoodItem createSampleFoodItem() {
        FoodItem foodItem = new FoodItem();
        foodItem.setFoodid(1);
        foodItem.setName("苹果");
        foodItem.setType("水果");
        foodItem.setCalories(52);
        foodItem.setFat(0.2);
        foodItem.setProtein(0.3);
        foodItem.setCarbohydrates(13.8);
        foodItem.setDietaryFiber(2.4);
        foodItem.setPotassium(107.0);
        foodItem.setSodium(1.0);
        return foodItem;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllFoodItems_Success() {
        // 准备测试数据
        FoodItem food1 = createSampleFoodItem();
        FoodItem food2 = createSampleFoodItem();
        food2.setFoodid(2);
        food2.setName("香蕉");
        food2.setCalories(89);

        List<FoodItem> expectedFoodItems = Arrays.asList(food1, food2);
        when(foodMapper.findAll()).thenReturn(expectedFoodItems);

        // 执行测试
        List<FoodItem> actualFoodItems = foodService.getAllFoodItems();

        // 验证结果
        assertNotNull(actualFoodItems);
        assertEquals(expectedFoodItems.size(), actualFoodItems.size());
        assertEquals(expectedFoodItems, actualFoodItems);
    }

    @Test
    void getFoodItemByName_Success() {
        // 准备测试数据
        FoodItem expectedFood = createSampleFoodItem();
        when(foodMapper.findByName(expectedFood.getName())).thenReturn(expectedFood);

        // 执行测试
        FoodItem actualFood = foodService.getFoodItemByName(expectedFood.getName());

        // 验证结果
        assertNotNull(actualFood);
        assertEquals(expectedFood.getFoodid(), actualFood.getFoodid());
        assertEquals(expectedFood.getName(), actualFood.getName());
        assertEquals(expectedFood.getType(), actualFood.getType());
        assertEquals(expectedFood.getCalories(), actualFood.getCalories());
        assertEquals(expectedFood.getFat(), actualFood.getFat());
        assertEquals(expectedFood.getProtein(), actualFood.getProtein());
        assertEquals(expectedFood.getCarbohydrates(), actualFood.getCarbohydrates());
        assertEquals(expectedFood.getDietaryFiber(), actualFood.getDietaryFiber());
        assertEquals(expectedFood.getPotassium(), actualFood.getPotassium());
        assertEquals(expectedFood.getSodium(), actualFood.getSodium());
    }

    @Test
    void addFoodItem_Success() throws Exception {
        // 准备测试数据
        FoodItem foodItem = createSampleFoodItem();
        when(foodMapper.findByName(foodItem.getName())).thenReturn(null);

        // 执行测试
        assertDoesNotThrow(() -> foodService.addFoodItem(foodItem));

        // 验证调用
        verify(foodMapper, times(1)).insert(foodItem);
    }

    @Test
    void addFoodItem_DuplicateName() {
        // 准备测试数据
        FoodItem foodItem = createSampleFoodItem();
        when(foodMapper.findByName(foodItem.getName())).thenReturn(createSampleFoodItem());

        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class,
                () -> foodService.addFoodItem(foodItem));
        assertEquals("添加食物失败: 食物名称已存在", exception.getMessage());
    }

    @Test
    void updateFoodItem_Success() throws Exception {
        // 准备测试数据
        FoodItem foodItem = createSampleFoodItem();
        foodItem.setName("更新后的苹果");
        foodItem.setCalories(55);

        when(foodMapper.findById(foodItem.getFoodid())).thenReturn(createSampleFoodItem());
        when(foodMapper.findByName(foodItem.getName())).thenReturn(null);
        when(foodMapper.update(foodItem)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> foodService.updateFoodItem(foodItem));

        // 验证调用
        verify(foodMapper, times(1)).update(foodItem);
    }

    @Test
    void updateFoodItem_NotFound() {
        // 准备测试数据
        FoodItem foodItem = createSampleFoodItem();
        when(foodMapper.findById(foodItem.getFoodid())).thenReturn(null);

        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class,
                () -> foodService.updateFoodItem(foodItem));
        assertEquals("更新食物失败: 食物不存在", exception.getMessage());
    }

    @Test
    void updateFoodItem_DuplicateName() {
        // 准备测试数据
        FoodItem foodItem = createSampleFoodItem();
        FoodItem existingFood = createSampleFoodItem();
        existingFood.setFoodid(2); // 不同的ID

        when(foodMapper.findById(foodItem.getFoodid())).thenReturn(createSampleFoodItem());
        when(foodMapper.findByName(foodItem.getName())).thenReturn(existingFood);

        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class,
                () -> foodService.updateFoodItem(foodItem));
        assertEquals("更新食物失败: 食物名称已存在", exception.getMessage());
    }

    @Test
    void deleteFoodItem_Success() throws Exception {
        // 准备测试数据
        Integer foodId = 1;
        when(foodMapper.findById(foodId)).thenReturn(createSampleFoodItem());
        when(foodMapper.delete(foodId)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> foodService.deleteFoodItem(foodId));

        // 验证调用
        verify(foodMapper, times(1)).delete(foodId);
    }

    @Test
    void findByNameLike_Success() {
        // 准备测试数据
        String name = "苹果";
        FoodItem expectedFood = createSampleFoodItem();
        when(foodMapper.findByNameLike(name)).thenReturn(expectedFood);

        // 执行测试
        FoodItem actualFood = foodService.findByNameLike(name);

        // 验证结果
        assertNotNull(actualFood);
        assertEquals(expectedFood.getFoodid(), actualFood.getFoodid());
        assertEquals(expectedFood.getName(), actualFood.getName());
        assertEquals(expectedFood.getType(), actualFood.getType());
        assertEquals(expectedFood.getCalories(), actualFood.getCalories());
        assertEquals(expectedFood.getFat(), actualFood.getFat());
        assertEquals(expectedFood.getProtein(), actualFood.getProtein());
        assertEquals(expectedFood.getCarbohydrates(), actualFood.getCarbohydrates());
        assertEquals(expectedFood.getDietaryFiber(), actualFood.getDietaryFiber());
        assertEquals(expectedFood.getPotassium(), actualFood.getPotassium());
        assertEquals(expectedFood.getSodium(), actualFood.getSodium());
    }
}