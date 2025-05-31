DROP TABLE IF EXISTS exercise_item; -- 添加清理语句
CREATE TABLE exercise_item (
                               exercise_id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               calories_per_hour INT NOT NULL
);