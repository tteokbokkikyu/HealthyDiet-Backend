package com.hd.hd_backend.mapper;

import com.hd.hd_backend.entity.ExerciseRecord;
import com.hd.hd_backend.dto.ExerciseRecordDTO;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/test-schema-exercise.sql", "/test-exercise2-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ExerciseRecordMapperTest {

    @Autowired
    private ExerciseRecordMapper mapper;

    @Test
    void addExerciseRecord_ShouldInsertRecordSuccessfully() {
        ExerciseRecord record = new ExerciseRecord();
        record.setDate("2025-05-21");
        record.setDuration("00:45:00");
        record.setExerciseId(1);
        record.setUserId(1);
        record.setBurnedCaloris(350); // 保持字段名一致

        mapper.addExerciseRecord(record);

        List<ExerciseRecordDTO> records = mapper.getExerciseRecordsByUserId(1);
        assertFalse(records.isEmpty());
        assertEquals(3, records.size());
        assertEquals("00:45:00", records.get(records.size() - 1).getDuration());
    }

    @Test
    void getExerciseRecord_ShouldReturnDTO_WhenIdExists() {
        ExerciseRecordDTO dto = mapper.getExerciseRecord(1);
        assertNotNull(dto);
        assertEquals("跑步", dto.getExerciseName());
        assertEquals("00:30:00", dto.getDuration());
    }

    @Test
    void updateExerciseRecord_ShouldModifySuccessfully() {
        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseRecordId(1);
        record.setDate("2025-05-22");
        record.setDuration("01:10:00");
        record.setExerciseId(1);
        record.setUserId(1);
        record.setBurnedCaloris(500); // 注意字段名一致性

        mapper.updateExerciseRecord(record);

        ExerciseRecordDTO updated = mapper.getExerciseRecord(1);
        assertEquals("01:10:00", updated.getDuration());
        assertEquals(500, updated.getBurnedCaloris());
    }

    @Test
    void deleteExerciseRecord_ShouldRemoveSuccessfully() {
        mapper.deleteExerciseRecord(2);

        ExerciseRecordDTO dto = mapper.getExerciseRecord(2);
        assertNull(dto);
    }

    @Test
    void getExerciseRecordsByUserId_ShouldReturnAllRecordsForUser() {
        List<ExerciseRecordDTO> records = mapper.getExerciseRecordsByUserId(1);
        assertEquals(2, records.size());
    }

    @Test
    void getExerciseRecords_ShouldReturnAllRecords() {
        List<ExerciseRecord> all = mapper.getExerciseRecords();
        assertTrue(all.size() >= 2);
    }
}