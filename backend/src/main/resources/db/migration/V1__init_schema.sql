-- V1__init_schema.sql: Initial schema for Blood Matcher system

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

CREATE TABLE donor_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    blood_group VARCHAR(10) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    last_donation_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donor_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_donor_blood_group ON donor_profiles(blood_group);
CREATE INDEX idx_donor_available ON donor_profiles(available);

CREATE TABLE blood_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    blood_group VARCHAR(10) NOT NULL,
    units_requested INT NOT NULL,
    hospital_name VARCHAR(150) NOT NULL,
    hospital_location VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    urgency VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    required_date DATE,
    notes VARCHAR(500),
    status VARCHAR(25) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_blood_requests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_requests_user ON blood_requests(user_id);
CREATE INDEX idx_requests_status ON blood_requests(status);
CREATE INDEX idx_requests_blood_group ON blood_requests(blood_group);

CREATE TABLE donor_invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    donor_id BIGINT NOT NULL,
    compatibility_outcome VARCHAR(150) NOT NULL,
    distance_km DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    invited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,
    CONSTRAINT uk_invitation_request_donor UNIQUE (request_id, donor_id),
    CONSTRAINT fk_invitations_request FOREIGN KEY (request_id) REFERENCES blood_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_donor FOREIGN KEY (donor_id) REFERENCES donor_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_invitations_request ON donor_invitations(request_id);
CREATE INDEX idx_invitations_donor ON donor_invitations(donor_id);
CREATE INDEX idx_invitations_status ON donor_invitations(status);

CREATE TABLE donation_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NULL,
    donor_id BIGINT NOT NULL,
    donation_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donations_request FOREIGN KEY (request_id) REFERENCES blood_requests(id) ON DELETE SET NULL,
    CONSTRAINT fk_donations_donor FOREIGN KEY (donor_id) REFERENCES donor_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_donations_donor ON donation_records(donor_id);
CREATE INDEX idx_donations_request ON donation_records(request_id);

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    delivery_status VARCHAR(50) DEFAULT 'IN_APP_SAVED',
    request_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
