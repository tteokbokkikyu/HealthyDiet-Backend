-- 插入运动项目
INSERT INTO exercise_item (name, calories_per_hour) VALUES ('跑步', 700);
INSERT INTO exercise_item (name, calories_per_hour) VALUES ('游泳', 600);

-- 插入运动记录
INSERT INTO exercise_record (date, duration, exercise_id, burned_caloris, user_id)
VALUES ('2025-05-20', '00:30:00', 1, 300, 1);

INSERT INTO exercise_record (date, duration, exercise_id, burned_caloris, user_id)
VALUES ('2025-05-19', '01:00:00', 2, 500, 1);