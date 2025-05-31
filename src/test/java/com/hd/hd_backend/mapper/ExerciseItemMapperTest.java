package com.hd.hd_backend.mapper;

import com.hd.hd_backend.entity.ExerciseItem;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseItemMapperTest {

    @Autowired
    private ExerciseItemMapper exerciseItemMapper;

    // 预定义测试数据ID
    private static final int EXISTING_ITEM_ID = 1;
    private static final int NON_EXISTENT_ID = 999;

    @Test
    @Sql(scripts = {"/test-schema-exerciseitem.sql", "/test-exercise-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllExerciseItems_ShouldReturnAllItems() {
        List<ExerciseItem> items = exerciseItemMapper.getAllExerciseItems();

        assertEquals(2, items.size());
        assertExerciseItemExists(items, "Running", 600);
        assertExerciseItemExists(items, "Swimming", 500);
    }

    @Test
    @Sql(scripts = {"/test-schema-exerciseitem.sql", "/test-exercise-data.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getExerciseItemById_ShouldReturnItem_WhenIdExists() {
        ExerciseItem item = exerciseItemMapper.getExerciseItemById(EXISTING_ITEM_ID);

        assertNotNull(item);
        assertEquals("Running", item.getName());
        assertEquals(600, item.getCaloriesPerHour());
    }

    @Test
    @Sql(scripts = "/test-schema-exerciseitem.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getExerciseItemById_ShouldReturnNull_WhenIdNotExist() {
        assertNull(exerciseItemMapper.getExerciseItemById(NON_EXISTENT_ID));
    }

    @Test
    @Sql(scripts = "/test-schema-exerciseitem.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void insertExerciseItem_ShouldPersistWithAutoIncrementId() {
        ExerciseItem newItem = createTestItem("Cycling", 400);

        int beforeCount = exerciseItemMapper.getAllExerciseItems().size();
        exerciseItemMapper.insertExerciseItem(newItem);
        int afterCount = exerciseItemMapper.getAllExerciseItems().size();

        assertEquals(beforeCount + 1, afterCount);

        ExerciseItem persistedItem = findItemByName("Cycling");
        assertNotNull(persistedItem);
        assertTrue(persistedItem.getExerciseId() > 0);
        assertEquals(400, persistedItem.getCaloriesPerHour());
    }


    // 辅助方法
    private ExerciseItem createTestItem(String name, int calories) {
        ExerciseItem item = new ExerciseItem();
        item.setName(name);
        item.setCaloriesPerHour(calories);
        return item;
    }

    private void assertExerciseItemExists(List<ExerciseItem> items, String name, int calories) {
        assertTrue(items.stream().anyMatch(item ->
                item.getName().equals(name) &&
                        item.getCaloriesPerHour() == calories
        ), "未找到项目: " + name);
    }

    private ExerciseItem findItemByName(String name) {
        return exerciseItemMapper.getAllExerciseItems().stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}