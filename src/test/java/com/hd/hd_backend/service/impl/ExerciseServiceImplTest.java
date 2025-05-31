package com.hd.hd_backend.service.impl;

import com.hd.hd_backend.dto.ExerciseRecordDTO;
import com.hd.hd_backend.entity.ExerciseItem;
import com.hd.hd_backend.entity.ExerciseRecord;
import com.hd.hd_backend.mapper.ExerciseItemMapper;
import com.hd.hd_backend.mapper.ExerciseRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseServiceImplTest {

    @Mock
    private ExerciseItemMapper exerciseItemMapper;

    @Mock
    private ExerciseRecordMapper exerciseRecordMapper;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllExerciseItem_ShouldReturnList() {
        when(exerciseItemMapper.getAllExerciseItems()).thenReturn(List.of(
                new ExerciseItem() {{
                    setName("跑步");
                    setCaloriesPerHour(600);
                }},
                new ExerciseItem() {{
                    setName("骑车");
                    setCaloriesPerHour(400);
                }}
        ));

        List<ExerciseItem> items = exerciseService.getAllExerciseItem();
        assertEquals(2, items.size());
        verify(exerciseItemMapper).getAllExerciseItems();
    }

    @Test
    void addExerciseItem_ShouldCallMapper() throws Exception {
        ExerciseItem item = new ExerciseItem();
        item.setName("游泳");
        item.setCaloriesPerHour(700);

        exerciseService.addExerciseItem(item);

        verify(exerciseItemMapper).insertExerciseItem(item);
    }

    @Test
    void addExerciseRecord_ShouldCalculateBurnedCaloriesAndInsert() throws Exception {
        ExerciseItem item = new ExerciseItem();
        item.setExerciseId(1);
        item.setCaloriesPerHour(600);

        when(exerciseItemMapper.getExerciseItemById(1)).thenReturn(item);

        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseId(1);
        record.setUserId(1);
        record.setDuration("01:30:00"); // 1.5小时

        exerciseService.addExerciseRecord(record);

        ArgumentCaptor<ExerciseRecord> captor = ArgumentCaptor.forClass(ExerciseRecord.class);
        verify(exerciseRecordMapper).addExerciseRecord(captor.capture());

        ExerciseRecord savedRecord = captor.getValue();
        // 1.5小时 * 600卡/小时 = 900卡
        assertEquals(900, savedRecord.getBurnedCaloris());
        assertEquals("01:30:00", savedRecord.getDuration());
    }

    @Test
    void addExerciseRecord_ShouldThrowException_WhenExerciseItemNotFound() {
        when(exerciseItemMapper.getExerciseItemById(999)).thenReturn(null);

        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseId(999);
        record.setUserId(1);
        record.setDuration("00:30:00");

        Exception exception = assertThrows(Exception.class, () -> {
            exerciseService.addExerciseRecord(record);
        });

        assertEquals("exercise item not found", exception.getMessage());
        verify(exerciseRecordMapper, never()).addExerciseRecord(any());
    }

    @Test
    void getUserExerciseRecord_ShouldReturnList() throws Exception {
        when(exerciseRecordMapper.getExerciseRecordsByUserId(1)).thenReturn(List.of(
                new ExerciseRecordDTO(),
                new ExerciseRecordDTO()
        ));

        List<ExerciseRecordDTO> records = exerciseService.getUserExerciseRecord(1);
        assertEquals(2, records.size());
        verify(exerciseRecordMapper).getExerciseRecordsByUserId(1);
    }

    @Test
    void deleteExerciseRecord_ShouldCallMapper() throws Exception {
        exerciseService.deleteExerciseRecord(1);
        verify(exerciseRecordMapper).deleteExerciseRecord(1);
    }
}
