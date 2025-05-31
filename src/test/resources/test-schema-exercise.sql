-- 删除旧表（如果存在）
DROP TABLE IF EXISTS exercise_record;
DROP TABLE IF EXISTS exercise_item;

CREATE TABLE exercise_item (
                               exercise_id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               calories_per_hour INT NOT NULL
);

-- 创建锻炼记录表
CREATE TABLE exercise_record (
                                 exercise_record_id INT PRIMARY KEY AUTO_INCREMENT,
                                 date VARCHAR(50),
                                 duration VARCHAR(50),
                                 exercise_id INT,
                                 burned_caloris INT,
                                 user_id INT,
                                 FOREIGN KEY (exercise_id) REFERENCES exercise_item(exercise_id)
);
