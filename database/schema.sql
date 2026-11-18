-- ============================================================
-- BlueMoon V1 - Database Schema (Revised, MySQL 8 Compatibility Patch)
-- PATCH NOTICE: MySQL 8.0.16+ compatibility fixes
-- 11 ON UPDATE CASCADE actions were changed to ON UPDATE RESTRICT
-- only on FKs whose child columns occur in CHECK constraints or are base
-- columns of STORED generated columns. Existing data/business rules unchanged.
-- This script creates tables but is not a non-destructive migration and is
-- not intended to be re-run against a partially created/production schema.
-- ============================================================
-- Target DBMS: MySQL 8.0.16+
-- Charset: utf8mb4
--
-- IMPORTANT:
-- 1. The DROP DATABASE statement is intentionally commented out.
-- 2. FeeObligation is created per APARTMENT, not per household.
-- 3. Mandatory fee payments are one-time/full payments in V1.
-- 4. Voluntary donations do not create fee obligations/debt.
-- 5. Payment records represent actual received payments only.
--    Pending QR instructions are stored in PaymentRequest.
-- ============================================================

-- DROP DATABASE IF EXISTS BlueMoonV1; -- Uncomment only when you intentionally want to erase all data.

CREATE DATABASE IF NOT EXISTS BlueMoonV1
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- Also normalize the database default when it already existed.
ALTER DATABASE BlueMoonV1
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE BlueMoonV1;
SET NAMES utf8mb4;

-- ============================================================
-- 1. PERSON AND ACCOUNT
-- ============================================================

CREATE TABLE Person (
    person_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NULL,
    email VARCHAR(254) NULL,
    identity_number VARCHAR(20) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (person_id),
    UNIQUE KEY uq_person_identity_number (identity_number),
    KEY idx_person_phone (phone)
) ENGINE=InnoDB;

CREATE TABLE Account (
    account_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    person_id BIGINT UNSIGNED NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('BQT', 'NV', 'RESIDENT') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT UNSIGNED NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id),
    UNIQUE KEY uq_account_person (person_id),
    UNIQUE KEY uq_account_username (username),
    KEY idx_account_created_by (created_by),
    KEY idx_account_updated_by (updated_by),
    CONSTRAINT fk_account_person
        FOREIGN KEY (person_id) REFERENCES Person (person_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_account_created_by
        FOREIGN KEY (created_by) REFERENCES Account (account_id)
        ON DELETE SET NULL ON UPDATE RESTRICT,
    CONSTRAINT fk_account_updated_by
        FOREIGN KEY (updated_by) REFERENCES Account (account_id)
        ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 2. APARTMENT, HOUSEHOLD AND RESIDENCE HISTORY
-- Household is retained as a supporting resident grouping only.
-- It is NOT the billing entity; mandatory fees are per apartment.
-- ============================================================

CREATE TABLE Apartment (
    apartment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    apartment_number VARCHAR(20) NOT NULL,
    area DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (apartment_id),
    UNIQUE KEY uq_apartment_number (apartment_number),
    CONSTRAINT chk_apartment_area_positive CHECK (area > 0)
) ENGINE=InnoDB;

CREATE TABLE Household (
    household_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    household_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (household_id),
    KEY idx_household_contact_phone (contact_phone)
) ENGINE=InnoDB;

CREATE TABLE HouseholdPerson (
    household_person_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    household_id BIGINT UNSIGNED NOT NULL,
    person_id BIGINT UNSIGNED NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    active_person_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (IF(end_date IS NULL, person_id, NULL)) STORED,
    PRIMARY KEY (household_person_id),
    UNIQUE KEY uq_household_person_start (household_id, person_id, start_date),
    UNIQUE KEY uq_person_one_active_household (active_person_id),
    KEY idx_household_person_person (person_id),
    CONSTRAINT chk_household_person_dates
        CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT fk_household_person_household
        FOREIGN KEY (household_id) REFERENCES Household (household_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_household_person_person
        FOREIGN KEY (person_id) REFERENCES Person (person_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE HouseholdApartment (
    assignment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    household_id BIGINT UNSIGNED NOT NULL,
    apartment_id BIGINT UNSIGNED NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    active_apartment_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (IF(end_date IS NULL, apartment_id, NULL)) STORED,
    active_household_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (IF(end_date IS NULL, household_id, NULL)) STORED,
    PRIMARY KEY (assignment_id),
    UNIQUE KEY uq_household_apartment_start (household_id, apartment_id, start_date),
    UNIQUE KEY uq_one_active_household_per_apartment (active_apartment_id),
    UNIQUE KEY uq_one_active_apartment_per_household (active_household_id),
    KEY idx_household_apartment_history (apartment_id, start_date),
    CONSTRAINT chk_household_apartment_dates
        CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT fk_household_apartment_apartment
        FOREIGN KEY (apartment_id) REFERENCES Apartment (apartment_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_household_apartment_household
        FOREIGN KEY (household_id) REFERENCES Household (household_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- Records actual residence events (move-in/out, temporary residence/absence).
-- This is an event history managed by BQT/NV, not a resident approval-request workflow.
CREATE TABLE ResidenceChange (
    residence_change_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    person_id BIGINT UNSIGNED NOT NULL,
    apartment_id BIGINT UNSIGNED NOT NULL,
    change_type ENUM('MOVE_IN', 'MOVE_OUT', 'TEMPORARY_RESIDENCE', 'TEMPORARY_ABSENCE') NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE NULL,
    notes TEXT NULL,
    recorded_by BIGINT UNSIGNED NOT NULL,
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (residence_change_id),
    KEY idx_residence_change_person_date (person_id, effective_from),
    KEY idx_residence_change_apartment_date (apartment_id, effective_from),
    KEY idx_residence_change_recorded_by (recorded_by),
    CONSTRAINT chk_residence_change_dates
        CHECK (effective_to IS NULL OR effective_to >= effective_from),
    CONSTRAINT fk_residence_change_person
        FOREIGN KEY (person_id) REFERENCES Person (person_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_residence_change_apartment
        FOREIGN KEY (apartment_id) REFERENCES Apartment (apartment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_residence_change_recorded_by
        FOREIGN KEY (recorded_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Optional resident-submitted request workflow added by agreement.
-- requested_changes stores the proposed field changes as JSON; the backend
-- must validate allowed fields and permissions before applying them.
-- If a request results in a residence event, link the created ResidenceChange.
CREATE TABLE ResidentChangeRequest (
    request_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    person_id BIGINT UNSIGNED NOT NULL,
    requester_account_id BIGINT UNSIGNED NOT NULL,
    request_type ENUM('PERSONAL_INFO_UPDATE', 'RESIDENCE_CHANGE') NOT NULL,
    requested_changes JSON NOT NULL,
    reason TEXT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT UNSIGNED NULL,
    reviewed_at DATETIME NULL,
    resolution_note TEXT NULL,
    applied_residence_change_id BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (request_id),
    UNIQUE KEY uq_request_applied_residence_change (applied_residence_change_id),
    KEY idx_resident_request_person_created (person_id, created_at),
    KEY idx_resident_request_status_created (status, created_at),
    KEY idx_resident_requester (requester_account_id),
    CONSTRAINT chk_resident_request_review_fields
        CHECK (
            (status = 'PENDING' AND reviewed_by IS NULL AND reviewed_at IS NULL)
            OR
            (status IN ('APPROVED', 'REJECTED') AND reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL)
            OR
            status = 'CANCELLED'
        ),
    CONSTRAINT fk_resident_request_person
        FOREIGN KEY (person_id) REFERENCES Person (person_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_resident_requester_account
        FOREIGN KEY (requester_account_id) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_resident_request_reviewed_by
        FOREIGN KEY (reviewed_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_resident_request_applied_change
        FOREIGN KEY (applied_residence_change_id) REFERENCES ResidenceChange (residence_change_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 3. MANDATORY FEE CAMPAIGNS AND FEE OBLIGATIONS
-- ============================================================

CREATE TABLE FeeType (
    fee_type_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    fee_name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    default_unit_price DECIMAL(14,2) NOT NULL,
    unit VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (fee_type_id),
    UNIQUE KEY uq_fee_type_name (fee_name),
    CONSTRAINT chk_fee_type_default_price_nonnegative
        CHECK (default_unit_price >= 0)
) ENGINE=InnoDB;

CREATE TABLE CollectionCampaign (
    campaign_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    fee_type_id BIGINT UNSIGNED NOT NULL,
    campaign_name VARCHAR(150) NOT NULL,
    fee_period CHAR(7) NOT NULL COMMENT 'Format YYYY-MM',
    applied_unit_price DECIMAL(14,2) NOT NULL,
    collection_start_date DATE NOT NULL,
    due_date DATE NOT NULL,
    description TEXT NULL,
    status ENUM('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (campaign_id),
    UNIQUE KEY uq_campaign_fee_type_period (fee_type_id, fee_period),
    KEY idx_campaign_status_due_date (status, due_date),
    KEY idx_campaign_created_by (created_by),
    CONSTRAINT chk_campaign_price_nonnegative CHECK (applied_unit_price >= 0),
    CONSTRAINT chk_campaign_dates CHECK (due_date >= collection_start_date),
    CONSTRAINT chk_campaign_period_format
        CHECK (fee_period REGEXP '^[0-9]{4}-(0[1-9]|1[0-2])$'),
    CONSTRAINT fk_campaign_fee_type
        FOREIGN KEY (fee_type_id) REFERENCES FeeType (fee_type_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_campaign_created_by
        FOREIGN KEY (created_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- One obligation per apartment for a mandatory fee campaign.
-- Household is deliberately not part of this table to avoid mismatched
-- household_id/apartment_id pairs and to follow the agreed billing scope.
CREATE TABLE FeeObligation (
    obligation_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    campaign_id BIGINT UNSIGNED NOT NULL,
    apartment_id BIGINT UNSIGNED NOT NULL,
    area_snapshot DECIMAL(10,2) NOT NULL,
    unit_price_snapshot DECIMAL(14,2) NOT NULL,
    amount_due DECIMAL(16,0) NOT NULL,
    payment_status ENUM('UNPAID', 'PAID', 'OVERDUE', 'CANCELLED') NOT NULL DEFAULT 'UNPAID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (obligation_id),
    UNIQUE KEY uq_obligation_campaign_apartment (campaign_id, apartment_id),
    UNIQUE KEY uq_obligation_id_apartment (obligation_id, apartment_id),
    KEY idx_obligation_apartment (apartment_id),
    KEY idx_obligation_status (payment_status),
    CONSTRAINT chk_obligation_area_positive CHECK (area_snapshot > 0),
    CONSTRAINT chk_obligation_unit_price_nonnegative CHECK (unit_price_snapshot >= 0),
    CONSTRAINT chk_obligation_amount_nonnegative CHECK (amount_due >= 0),
    CONSTRAINT fk_obligation_campaign
        FOREIGN KEY (campaign_id) REFERENCES CollectionCampaign (campaign_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_obligation_apartment
        FOREIGN KEY (apartment_id) REFERENCES Apartment (apartment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 4. VOLUNTARY DONATION CAMPAIGNS
-- No FeeObligation rows are created for donation campaigns.
-- Each actual donation is recorded in Payment.
-- ============================================================

CREATE TABLE DonationCampaign (
    donation_campaign_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    campaign_name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    target_amount DECIMAL(16,0) NULL,
    status ENUM('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (donation_campaign_id),
    KEY idx_donation_campaign_status_dates (status, start_date, end_date),
    KEY idx_donation_campaign_created_by (created_by),
    CONSTRAINT chk_donation_campaign_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_donation_campaign_target_nonnegative
        CHECK (target_amount IS NULL OR target_amount >= 0),
    CONSTRAINT fk_donation_campaign_created_by
        FOREIGN KEY (created_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 5. QR PAYMENT REQUESTS, PAYMENTS AND SEPAY TRANSACTIONS
-- ============================================================

-- A QR request is only an instruction to transfer money, not a payment receipt.
-- It can target either one mandatory obligation OR one donation campaign.
CREATE TABLE PaymentRequest (
    payment_request_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    apartment_id BIGINT UNSIGNED NOT NULL,
    obligation_id BIGINT UNSIGNED NULL,
    donation_campaign_id BIGINT UNSIGNED NULL,
    amount DECIMAL(16,0) NOT NULL,
    payment_reference VARCHAR(100) NOT NULL,
    status ENUM('PENDING', 'MATCHED', 'EXPIRED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    expires_at DATETIME NOT NULL,
    created_by BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    matched_at DATETIME NULL,
    active_obligation_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (IF(status = 'PENDING' AND obligation_id IS NOT NULL, obligation_id, NULL)) STORED,
    PRIMARY KEY (payment_request_id),
    UNIQUE KEY uq_payment_request_reference (payment_reference),
    UNIQUE KEY uq_one_pending_qr_per_obligation (active_obligation_id),
    KEY idx_payment_request_status_expiry (status, expires_at),
    KEY idx_payment_request_donation (donation_campaign_id, apartment_id),
    CONSTRAINT chk_payment_request_target
        CHECK (
            (obligation_id IS NOT NULL AND donation_campaign_id IS NULL)
            OR
            (obligation_id IS NULL AND donation_campaign_id IS NOT NULL)
        ),
    CONSTRAINT chk_payment_request_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_payment_request_expiry CHECK (expires_at > created_at),
    CONSTRAINT chk_payment_request_match_time
        CHECK (
            (status = 'MATCHED' AND matched_at IS NOT NULL)
            OR
            (status <> 'MATCHED' AND matched_at IS NULL)
        ),
    CONSTRAINT fk_payment_request_apartment
        FOREIGN KEY (apartment_id) REFERENCES Apartment (apartment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payment_request_obligation_apartment
        FOREIGN KEY (obligation_id, apartment_id)
        REFERENCES FeeObligation (obligation_id, apartment_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_request_donation_campaign
        FOREIGN KEY (donation_campaign_id) REFERENCES DonationCampaign (donation_campaign_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_request_created_by
        FOREIGN KEY (created_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- A Payment row is an actual received payment. For mandatory fees, one
-- confirmed payment must cover the full obligation (validated in backend).
-- A donation payment links directly to a DonationCampaign and apartment;
-- multiple actual donations can be recorded for the same apartment/campaign.
CREATE TABLE Payment (
    payment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    obligation_id BIGINT UNSIGNED NULL,
    donation_campaign_id BIGINT UNSIGNED NULL,
    apartment_id BIGINT UNSIGNED NOT NULL,
    payment_request_id BIGINT UNSIGNED NULL,
    payer_person_id BIGINT UNSIGNED NULL,
    amount DECIMAL(16,0) NOT NULL,
    payment_method ENUM('CASH', 'BANK_TRANSFER', 'QR') NOT NULL,
    payment_source ENUM('MANUAL', 'AUTOMATIC') NOT NULL DEFAULT 'MANUAL',
    payment_status ENUM('CONFIRMED', 'REVERSED') NOT NULL DEFAULT 'CONFIRMED',
    recorded_by BIGINT UNSIGNED NULL,
    paid_at DATETIME NOT NULL,
    reversed_by BIGINT UNSIGNED NULL,
    reversed_at DATETIME NULL,
    reversal_reason TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_obligation_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (IF(payment_status = 'CONFIRMED' AND obligation_id IS NOT NULL, obligation_id, NULL)) STORED,
    PRIMARY KEY (payment_id),
    UNIQUE KEY uq_one_confirmed_payment_per_obligation (confirmed_obligation_id),
    UNIQUE KEY uq_payment_request_once (payment_request_id),
    KEY idx_payment_apartment_paid_at (apartment_id, paid_at),
    KEY idx_payment_obligation_status (obligation_id, payment_status),
    KEY idx_payment_donation_status (donation_campaign_id, payment_status),
    KEY idx_payment_payer (payer_person_id),
    KEY idx_payment_recorded_by (recorded_by),
    CONSTRAINT chk_payment_target
        CHECK (
            (obligation_id IS NOT NULL AND donation_campaign_id IS NULL)
            OR
            (obligation_id IS NULL AND donation_campaign_id IS NOT NULL)
        ),
    CONSTRAINT chk_payment_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_payment_qr_requires_request
        CHECK (payment_method <> 'QR' OR payment_request_id IS NOT NULL),
    CONSTRAINT chk_payment_automatic_is_qr
        CHECK (payment_source <> 'AUTOMATIC' OR payment_method = 'QR'),
    CONSTRAINT chk_payment_manual_has_recorder
        CHECK (payment_source <> 'MANUAL' OR recorded_by IS NOT NULL),
    CONSTRAINT chk_payment_reversal_fields
        CHECK (
            (payment_status = 'CONFIRMED' AND reversed_by IS NULL AND reversed_at IS NULL)
            OR
            (payment_status = 'REVERSED' AND reversed_by IS NOT NULL AND reversed_at IS NOT NULL)
        ),
    CONSTRAINT fk_payment_obligation_apartment
        FOREIGN KEY (obligation_id, apartment_id)
        REFERENCES FeeObligation (obligation_id, apartment_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_donation_campaign
        FOREIGN KEY (donation_campaign_id) REFERENCES DonationCampaign (donation_campaign_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_apartment
        FOREIGN KEY (apartment_id) REFERENCES Apartment (apartment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payment_request
        FOREIGN KEY (payment_request_id) REFERENCES PaymentRequest (payment_request_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_payer_person
        FOREIGN KEY (payer_person_id) REFERENCES Person (person_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payment_recorded_by
        FOREIGN KEY (recorded_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_reversed_by
        FOREIGN KEY (reversed_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- Store every incoming SePay webhook transaction, including unmatched ones,
-- so repeated webhook delivery can be handled idempotently and exceptions
-- can be reviewed without falsely marking a payment as successful.
CREATE TABLE BankTransaction (
    bank_transaction_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    provider ENUM('SEPAY') NOT NULL DEFAULT 'SEPAY',
    external_transaction_id VARCHAR(100) NOT NULL,
    receiving_account_number VARCHAR(50) NOT NULL,
    direction ENUM('IN', 'OUT') NOT NULL,
    amount DECIMAL(16,0) NOT NULL,
    transfer_content VARCHAR(255) NULL,
    transaction_at DATETIME NOT NULL,
    received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_status ENUM('RECEIVED', 'MATCHED', 'UNMATCHED', 'INVALID') NOT NULL DEFAULT 'RECEIVED',
    payment_id BIGINT UNSIGNED NULL,
    raw_payload JSON NULL,
    processing_note TEXT NULL,
    PRIMARY KEY (bank_transaction_id),
    UNIQUE KEY uq_bank_transaction_external_id (external_transaction_id),
    UNIQUE KEY uq_bank_transaction_payment (payment_id),
    KEY idx_bank_transaction_status_received (processing_status, received_at),
    KEY idx_bank_transaction_time (transaction_at),
    CONSTRAINT chk_bank_transaction_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_bank_transaction_match_payment
        CHECK (
            (processing_status = 'MATCHED' AND payment_id IS NOT NULL)
            OR
            (processing_status <> 'MATCHED' AND payment_id IS NULL)
        ),
    CONSTRAINT fk_bank_transaction_payment
        FOREIGN KEY (payment_id) REFERENCES Payment (payment_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 6. AUDIT HISTORY FOR FEE OBLIGATION CHANGES
-- ============================================================

CREATE TABLE FeeObligationHistory (
    history_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    obligation_id BIGINT UNSIGNED NOT NULL,
    old_amount_due DECIMAL(16,0) NOT NULL,
    new_amount_due DECIMAL(16,0) NOT NULL,
    old_unit_price DECIMAL(14,2) NOT NULL,
    new_unit_price DECIMAL(14,2) NOT NULL,
    reason TEXT NOT NULL,
    changed_by BIGINT UNSIGNED NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    manual_resolution_note TEXT NULL,
    resolution_status ENUM('NOT_REQUIRED', 'PENDING', 'RESOLVED') NOT NULL DEFAULT 'NOT_REQUIRED',
    PRIMARY KEY (history_id),
    KEY idx_obligation_history_obligation_changed_at (obligation_id, changed_at),
    KEY idx_obligation_history_changed_by (changed_by),
    CONSTRAINT chk_obligation_history_amounts_nonnegative
        CHECK (old_amount_due >= 0 AND new_amount_due >= 0),
    CONSTRAINT chk_obligation_history_prices_nonnegative
        CHECK (old_unit_price >= 0 AND new_unit_price >= 0),
    CONSTRAINT fk_obligation_history_obligation
        FOREIGN KEY (obligation_id) REFERENCES FeeObligation (obligation_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_obligation_history_changed_by
        FOREIGN KEY (changed_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 7. NOTIFICATION SETTINGS AND NOTIFICATIONS
-- One global settings row is allowed in V1.
-- ============================================================

CREATE TABLE NotificationSetting (
    setting_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    setting_key ENUM('GLOBAL') NOT NULL DEFAULT 'GLOBAL',
    days_before_due INT NOT NULL DEFAULT 3,
    first_reminder_after_due INT NOT NULL DEFAULT 1,
    reminder_interval INT NOT NULL DEFAULT 3,
    max_reminders INT NOT NULL DEFAULT 3,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    updated_by BIGINT UNSIGNED NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (setting_id),
    UNIQUE KEY uq_notification_setting_key (setting_key),
    KEY idx_notification_setting_updated_by (updated_by),
    CONSTRAINT chk_notification_days_before_nonnegative CHECK (days_before_due >= 0),
    CONSTRAINT chk_notification_first_after_nonnegative CHECK (first_reminder_after_due >= 0),
    CONSTRAINT chk_notification_interval_positive CHECK (reminder_interval > 0),
    CONSTRAINT chk_notification_max_reminders_nonnegative CHECK (max_reminders >= 0),
    CONSTRAINT fk_notification_setting_updated_by
        FOREIGN KEY (updated_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Notification (
    notification_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    account_id BIGINT UNSIGNED NOT NULL,
    obligation_id BIGINT UNSIGNED NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    notification_type ENUM(
        'FEE_REMINDER',
        'PAYMENT_CONFIRMED',
        'PAYMENT_FAILED',
        'DONATION_RECEIVED',
        'SYSTEM'
    ) NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME NULL,
    PRIMARY KEY (notification_id),
    KEY idx_notification_account_read_sent (account_id, read_at, sent_at),
    KEY idx_notification_obligation (obligation_id),
    CONSTRAINT chk_notification_read_after_sent
        CHECK (read_at IS NULL OR read_at >= sent_at),
    CONSTRAINT fk_notification_account
        FOREIGN KEY (account_id) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_notification_obligation
        FOREIGN KEY (obligation_id) REFERENCES FeeObligation (obligation_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 8. COMPLAINTS AND ONE RESPONSE PER COMPLAINT
-- Aligned with V1 scope: no multi-message conversation or attachments.
-- ============================================================

CREATE TABLE Complaint (
    complaint_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    created_by_account_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('NEW', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'NEW',
    response_content TEXT NULL,
    responded_by BIGINT UNSIGNED NULL,
    responded_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (complaint_id),
    KEY idx_complaint_creator_created (created_by_account_id, created_at),
    KEY idx_complaint_status_created (status, created_at),
    KEY idx_complaint_responded_by (responded_by),
    CONSTRAINT chk_complaint_response_fields
        CHECK (
            (response_content IS NULL AND responded_by IS NULL AND responded_at IS NULL)
            OR
            (response_content IS NOT NULL AND responded_by IS NOT NULL AND responded_at IS NOT NULL)
        ),
    CONSTRAINT fk_complaint_creator
        FOREIGN KEY (created_by_account_id) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_complaint_responded_by
        FOREIGN KEY (responded_by) REFERENCES Account (account_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- END OF SCHEMA
--
-- Important application-level rules (cannot be fully enforced by simple
-- CHECK constraints because they compare values across related rows):
-- 1. For mandatory fees, Payment.amount must equal FeeObligation.amount_due.
-- 2. A CONFIRMED payment for a mandatory obligation changes its status to PAID.
--    When a payment is reversed, recalculate the obligation status.
-- 3. OVERDUE is derived from due_date and unpaid status; update it via a
--    scheduled job or calculate it in queries. Do not rely on manual entry.
-- 4. A PaymentRequest must match the same apartment and target as its Payment.
-- 5. A donation creates no FeeObligation. Payment rows linked to a
--    DonationCampaign are the actual contributions and are summed for reports.
-- 6. Validate SePay signature, receiving account, inbound direction, amount,
--    reference and provider transaction ID before creating a confirmed Payment.
-- 7. Account-role authorization (BQT/NV/RESIDENT) must be enforced in backend.
-- 8. Check date-range overlaps in HouseholdPerson/HouseholdApartment and
--    ResidenceChange in backend transactions; CHECK constraints alone cannot
--    prevent overlapping intervals across different rows.
-- ============================================================
