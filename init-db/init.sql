CREATE DATABASE IF NOT EXISTS messaging_db;
USE messaging_db;

CREATE TABLE IF NOT EXISTS origins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO origins (phone_number, description, active) VALUES
    ('+573001234567', 'Origin Line 1 - Main', true),
    ('+573009876543', 'Origin Line 2 - Secondary', true),
    ('+573005551234', 'Origin Line 3 - Marketing', true),
    ('+573007779999', 'Origin Line 4 - Support', true),
    ('+573002468135', 'Origin Line 5 - Notifications', true);

-- User creation is handled by MYSQL_USER and MYSQL_PASSWORD environment variables in Docker Compose.
-- MySQL Docker image automatically creates the user and grants privileges on MYSQL_DATABASE.
