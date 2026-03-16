-- schema.sql
-- Job Application Tracker (MySQL 8.0)

CREATE DATABASE IF NOT EXISTS job_tracker
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE job_tracker;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- 1) user
CREATE TABLE `user` (
  uuid CHAR(36) PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2) company
CREATE TABLE company (
  cuid CHAR(36) PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  industry VARCHAR(100) NULL,
  location_city VARCHAR(100) NULL,
  location_state VARCHAR(50) NULL,
  company_url VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_company_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) job
CREATE TABLE job (
  juid CHAR(36) PRIMARY KEY,
  cuid CHAR(36) NOT NULL,
  title VARCHAR(150) NOT NULL,
  employment_type VARCHAR(30) NOT NULL,
  work_type VARCHAR(30) NOT NULL,
  job_url VARCHAR(255) NULL,
  salary_min INT NULL,
  salary_max INT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_job_company
    FOREIGN KEY (cuid) REFERENCES company(cuid)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

  CONSTRAINT chk_job_salary_min_nonneg CHECK (salary_min IS NULL OR salary_min >= 0),
  CONSTRAINT chk_job_salary_max_nonneg CHECK (salary_max IS NULL OR salary_max >= 0),
  CONSTRAINT chk_job_salary_range CHECK (
    salary_min IS NULL OR salary_max IS NULL OR salary_max >= salary_min
  ),
  CONSTRAINT chk_job_employment_type CHECK (
    employment_type IN ('internship','full_time','contract','part_time')
  ),
  CONSTRAINT chk_job_work_type CHECK (
    work_type IN ('remote','hybrid','on_site')
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4) application
CREATE TABLE application (
  auid CHAR(36) PRIMARY KEY,
  uuid CHAR(36) NOT NULL,
  juid CHAR(36) NOT NULL,
  status VARCHAR(30) NOT NULL,
  applied_at DATETIME NOT NULL,
  source VARCHAR(50) NULL,
  notes TEXT NULL,
  last_updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_application_user
    FOREIGN KEY (uuid) REFERENCES `user`(uuid)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

  CONSTRAINT fk_application_job
    FOREIGN KEY (juid) REFERENCES job(juid)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

  CONSTRAINT uq_application_user_job UNIQUE (uuid, juid),

  CONSTRAINT chk_application_status CHECK (
    status IN ('applied','phone_screen','interview','offer','rejected','withdrawn')
  ),
  CONSTRAINT chk_application_source CHECK (
    source IS NULL OR source IN ('linkedin','handshake','referral','company_site','other')
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5) activity
CREATE TABLE activity (
  actuid CHAR(36) PRIMARY KEY,
  auid CHAR(36) NOT NULL,
  uuid CHAR(36) NOT NULL,
  event_type VARCHAR(40) NOT NULL,
  old_status VARCHAR(30) NULL,
  new_status VARCHAR(30) NULL,
  event_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  details TEXT NULL,

  CONSTRAINT fk_activity_application
    FOREIGN KEY (auid) REFERENCES application(auid)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_activity_user
    FOREIGN KEY (uuid) REFERENCES `user`(uuid)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

  CONSTRAINT chk_activity_event_type CHECK (
    event_type IN ('created','status_change','note_added','interview_scheduled','followup_set')
  ),
  CONSTRAINT chk_activity_status_change_requires_statuses CHECK (
    event_type <> 'status_change'
    OR (old_status IS NOT NULL AND new_status IS NOT NULL)
  ),
  CONSTRAINT chk_activity_old_status_allowed CHECK (
    old_status IS NULL OR old_status IN ('applied','phone_screen','interview','offer','rejected','withdrawn')
  ),
  CONSTRAINT chk_activity_new_status_allowed CHECK (
    new_status IS NULL OR new_status IN ('applied','phone_screen','interview','offer','rejected','withdrawn')
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Indexes for common joins/lookups
CREATE INDEX idx_job_cuid ON job(cuid);

CREATE INDEX idx_application_uuid ON application(uuid);
CREATE INDEX idx_application_juid ON application(juid);
CREATE INDEX idx_application_status ON application(status);
CREATE INDEX idx_application_applied_at ON application(applied_at);

CREATE INDEX idx_activity_auid ON activity(auid);
CREATE INDEX idx_activity_uuid ON activity(uuid);
CREATE INDEX idx_activity_event_type ON activity(event_type);
CREATE INDEX idx_activity_event_time ON activity(event_time);
