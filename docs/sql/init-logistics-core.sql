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
