-- 创建用户表(与生产环境一致)
CREATE TABLE IF NOT EXISTS user (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `name` varchar(20) NOT NULL,
                                    `password` varchar(255) DEFAULT NULL,
                                    `profile_picture` varchar(500) DEFAULT NULL,
                                    `is_admin` int NOT NULL DEFAULT '0',
                                    `phone` varchar(15) NOT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `user_unique` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3;
-- 创建普通用户表(与生产环境一致)
CREATE TABLE IF NOT EXISTS normal_user (
                                           user_id INT NOT NULL,
                                           weight DOUBLE DEFAULT NULL,
                                           age INT DEFAULT NULL,
                                           height INT DEFAULT NULL,
                                           is_blocked INT DEFAULT 0,
                                           gender INT DEFAULT NULL,
                                           activity_factor DOUBLE DEFAULT NULL,
                                           PRIMARY KEY (user_id),
                                           CONSTRAINT normal_user_user_FK FOREIGN KEY (user_id) REFERENCES user (id)
                                               ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;