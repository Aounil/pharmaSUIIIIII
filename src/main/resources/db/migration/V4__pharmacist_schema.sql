-- V4: Pharmacist workflow schema additions
-- Add pharmacist-specific prescription fields without modifying existing admin schema.

ALTER TABLE prescriptions
    ADD COLUMN file_path VARCHAR(500) NULL;

ALTER TABLE prescriptions
    ADD COLUMN total_amount DECIMAL(10,2) NULL;

ALTER TABLE prescriptions
    ADD COLUMN rejection_comment TEXT NULL;

ALTER TABLE prescriptions
    ADD COLUMN updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP;
