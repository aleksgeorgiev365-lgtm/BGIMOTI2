-- ══════════════════════════════════════════════════
--  БГИмоти — MySQL Database Schema
--  Изпълнете тези команди в MySQL преди стартиране
-- ══════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS bgimoti
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bgimoti;

-- ── Потребители (Users) ──────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(120)    NOT NULL,
    email       VARCHAR(180)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,        -- BCrypt hashed
    role        ENUM('CLIENT','BROKER','ADMIN') NOT NULL DEFAULT 'CLIENT',
    license_no  VARCHAR(50)     NULL,            -- само за брокери
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      BOOLEAN         NOT NULL DEFAULT TRUE
);

-- ── Имоти (Properties) ──────────────────────────
CREATE TABLE IF NOT EXISTS properties (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    type            ENUM('APARTMENT','HOUSE','COMMERCIAL','LAND') NOT NULL,
    transaction     ENUM('SALE','RENT') NOT NULL DEFAULT 'SALE',
    price           DECIMAL(14,2)   NOT NULL,
    area            DECIMAL(10,2)   NOT NULL,
    rooms           INT             NULL,
    floor           INT             NULL,
    total_floors    INT             NULL,
    city            VARCHAR(100)    NOT NULL,
    district        VARCHAR(100)    NOT NULL,
    address         VARCHAR(255)    NULL,
    description     TEXT            NULL,
    features        TEXT            NULL,        -- JSON масив
    badge           VARCHAR(50)     NULL,        -- 'new', 'rent', ''
    broker_id       BIGINT          NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    FOREIGN KEY (broker_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Запазени обяви (Saved Listings) ─────────────
CREATE TABLE IF NOT EXISTS saved_listings (
    id          BIGINT  AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT  NOT NULL,
    property_id BIGINT  NOT NULL,
    saved_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_save (user_id, property_id),
    FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- ── Оценки на брокери (Broker Ratings) ──────────
CREATE TABLE IF NOT EXISTS broker_ratings (
    id          BIGINT  AUTO_INCREMENT PRIMARY KEY,
    broker_id   BIGINT  NOT NULL,
    client_id   BIGINT  NOT NULL,
    property_id BIGINT  NULL,
    rating      TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT    NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_rating (broker_id, client_id, property_id),
    FOREIGN KEY (broker_id)   REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (client_id)   REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL
);

-- ── Съобщения (Messages) ────────────────────────
CREATE TABLE IF NOT EXISTS messages (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    sender_name VARCHAR(120) NOT NULL,
    sender_phone VARCHAR(30) NULL,
    sender_email VARCHAR(180) NULL,
    broker_id   BIGINT      NOT NULL,
    property_id BIGINT      NULL,
    body        TEXT        NOT NULL,
    sent_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read     BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (broker_id)   REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL
);
