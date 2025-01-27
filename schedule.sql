use scheduleapp;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255),
    user_email VARCHAR(255) UNIQUE
);

CREATE TABLE schedules (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    task VARCHAR(200),
    username VARCHAR(10),
    user_email VARCHAR(255),
    password VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);