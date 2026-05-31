-- V4: Pharmacist workflow schema additions
-- Add pharmacist-specific prescription fields without modifying existing admin schema.

ALTER TABLE prescriptions
    ADD COLUMN IF NOT EXISTS file_path VARCHAR(500) NULL;

ALTER TABLE prescriptions
    ADD COLUMN IF NOT EXISTS total_amount DECIMAL(10,2) NULL;

ALTER TABLE prescriptions
    ADD COLUMN IF NOT EXISTS rejection_comment TEXT NULL;

ALTER TABLE prescriptions
    ADD COLUMN IF NOT EXISTS updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP;
