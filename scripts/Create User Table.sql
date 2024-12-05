use mydb;

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    sex VARCHAR(50),
    email VARCHAR(255) NOT NULL
);