CREATE TABLE equipment (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(50) NOT NULL UNIQUE,
                           type VARCHAR(30) NOT NULL,
                           status VARCHAR(10) NOT NULL DEFAULT 'STOPPED',
                           updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
);

CREATE TABLE equipment_status_log (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      equipment_id BIGINT NOT NULL,
                                      status VARCHAR(10) NOT NULL,
                                      started_at DATETIME(3) NOT NULL,
                                      ended_at DATETIME(3) NULL,
                                      CONSTRAINT fk_log_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id),
                                      INDEX idx_log_equipment_started (equipment_id, started_at)
);

CREATE TABLE production_record (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   recorded_at DATETIME(3) NOT NULL,
                                   planned_kg DECIMAL(10,2) NOT NULL,
                                   actual_kg DECIMAL(10,2) NOT NULL,
                                   good_kg DECIMAL(10,2) NOT NULL,
                                   reject_kg DECIMAL(10,2) NOT NULL,
                                   INDEX idx_prod_recorded (recorded_at)
);

CREATE TABLE oee_snapshot (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              recorded_at DATETIME(3) NOT NULL,
                              availability DECIMAL(5,2) NOT NULL,
                              performance DECIMAL(5,2) NOT NULL,
                              quality DECIMAL(5,2) NOT NULL,
                              oee DECIMAL(5,2) NOT NULL,
                              INDEX idx_oee_recorded (recorded_at)
);

CREATE TABLE alarm (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       equipment_id BIGINT NOT NULL,
                       area VARCHAR(50) NOT NULL,
                       message VARCHAR(200) NOT NULL,
                       level VARCHAR(10) NOT NULL,
                       raised_at DATETIME(3) NOT NULL,
                       acknowledged_at DATETIME(3) NULL,
                       CONSTRAINT fk_alarm_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id),
                       INDEX idx_alarm_raised (raised_at)
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
);