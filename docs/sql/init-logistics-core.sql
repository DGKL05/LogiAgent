CREATE DATABASE IF NOT EXISTS logiagent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE logiagent;

CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT NOT NULL PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_name VARCHAR(64) NOT NULL,
    receiver_phone VARCHAR(32) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    goods_name VARCHAR(128) NULL,
    weight DECIMAL(10, 2) NULL,
    status VARCHAR(32) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_waybill (
    id BIGINT NOT NULL PRIMARY KEY,
    waybill_no VARCHAR(32) NOT NULL,
    order_no VARCHAR(32) NOT NULL,
    current_status VARCHAR(32) NOT NULL,
    exception_type VARCHAR(32) NULL,
    exception_reason VARCHAR(255) NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_waybill_no (waybill_no),
    UNIQUE KEY uk_waybill_order_no (order_no),
    KEY idx_waybill_status (current_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_track (
    id BIGINT NOT NULL PRIMARY KEY,
    waybill_no VARCHAR(32) NOT NULL,
    action VARCHAR(32) NOT NULL,
    description VARCHAR(255) NULL,
    operator_id BIGINT NULL,
    event_time DATETIME NOT NULL,
    create_time DATETIME NOT NULL,
    KEY idx_track_waybill_time (waybill_no, event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_station (
    id BIGINT NOT NULL PRIMARY KEY,
    station_name VARCHAR(64) NOT NULL,
    city VARCHAR(64) NOT NULL,
    address VARCHAR(255) NULL,
    longitude DECIMAL(10, 6) NULL,
    latitude DECIMAL(10, 6) NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    KEY idx_station_name (station_name),
    KEY idx_station_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_route (
    id BIGINT NOT NULL PRIMARY KEY,
    start_station_id BIGINT NOT NULL,
    end_station_id BIGINT NOT NULL,
    distance_km DECIMAL(10, 2) NOT NULL,
    duration_minute DECIMAL(10, 2) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    KEY idx_route_start (start_station_id),
    KEY idx_route_end (end_station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_station (id, station_name, city, address, longitude, latitude, create_time, update_time)
VALUES
    (1, '广州网点', 'Guangzhou', 'Guangzhou logistics station', 113.264385, 23.129110, NOW(), NOW()),
    (2, '东莞中转站', 'Dongguan', 'Dongguan transfer station', 113.751799, 23.020673, NOW(), NOW()),
    (3, '深圳网点', 'Shenzhen', 'Shenzhen logistics station', 114.057865, 22.543096, NOW(), NOW()),
    (4, '佛山网点', 'Foshan', 'Foshan logistics station', 113.121436, 23.021479, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    station_name = VALUES(station_name),
    city = VALUES(city),
    address = VALUES(address),
    longitude = VALUES(longitude),
    latitude = VALUES(latitude),
    update_time = NOW();

INSERT INTO t_route (id, start_station_id, end_station_id, distance_km, duration_minute, cost, enabled, create_time, update_time)
VALUES
    (1, 1, 2, 65.30, 80.00, 40.00, 1, NOW(), NOW()),
    (2, 2, 3, 78.60, 95.00, 45.00, 1, NOW(), NOW()),
    (3, 1, 4, 28.50, 45.00, 20.00, 1, NOW(), NOW()),
    (4, 4, 3, 135.20, 160.00, 70.00, 1, NOW(), NOW()),
    (5, 1, 3, 145.00, 170.00, 98.00, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    start_station_id = VALUES(start_station_id),
    end_station_id = VALUES(end_station_id),
    distance_km = VALUES(distance_km),
    duration_minute = VALUES(duration_minute),
    cost = VALUES(cost),
    enabled = VALUES(enabled),
    update_time = NOW();

CREATE TABLE IF NOT EXISTS t_dispatch_task (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(64) NOT NULL,
    waybill_no VARCHAR(64) NOT NULL,
    courier_id BIGINT NULL,
    station_id BIGINT NOT NULL,
    task_status VARCHAR(32) NOT NULL,
    assign_time DATETIME NULL,
    finish_time DATETIME NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_dispatch_task_no (task_no),
    KEY idx_dispatch_station_status (station_id, task_status),
    KEY idx_dispatch_waybill_no (waybill_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_dispatch_task (id, task_no, waybill_no, courier_id, station_id, task_status, assign_time, finish_time, create_time, update_time)
VALUES
    (1, 'DT202605100001', 'WB202605100001', 101, 1, 'CREATED', NOW(), NULL, NOW(), NOW()),
    (2, 'DT202605100002', 'WB202605100002', 102, 1, 'ASSIGNED', NOW(), NULL, NOW(), NOW()),
    (3, 'DT202605100003', 'WB202605100003', 103, 2, 'DELIVERING', NOW(), NULL, NOW(), NOW()),
    (4, 'DT202605100004', 'WB202605100004', 104, 3, 'CREATED', NOW(), NULL, NOW(), NOW()),
    (5, 'DT202605100005', 'WB202605100005', 105, 3, 'CREATED', NOW(), NULL, NOW(), NOW()),
    (6, 'DT202605100006', 'WB202605100006', 106, 3, 'CREATED', NOW(), NULL, NOW(), NOW()),
    (7, 'DT202605100007', 'WB202605100007', 107, 4, 'FINISHED', NOW(), NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE
    waybill_no = VALUES(waybill_no),
    courier_id = VALUES(courier_id),
    station_id = VALUES(station_id),
    task_status = VALUES(task_status),
    assign_time = VALUES(assign_time),
    finish_time = VALUES(finish_time),
    update_time = NOW();

CREATE TABLE IF NOT EXISTS t_agent_session (
    id BIGINT NOT NULL PRIMARY KEY,
    session_id VARCHAR(40) NOT NULL,
    user_id BIGINT NULL,
    question VARCHAR(512) NOT NULL,
    intent VARCHAR(64) NOT NULL,
    answer TEXT NOT NULL,
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_agent_session_id (session_id),
    KEY idx_agent_session_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_agent_tool_log (
    id BIGINT NOT NULL PRIMARY KEY,
    session_id VARCHAR(40) NOT NULL,
    tool_name VARCHAR(128) NOT NULL,
    request_params TEXT NULL,
    response_result TEXT NULL,
    success TINYINT(1) NOT NULL,
    error_msg TEXT NULL,
    cost_ms BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    KEY idx_agent_tool_log_session_id (session_id),
    KEY idx_agent_tool_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
