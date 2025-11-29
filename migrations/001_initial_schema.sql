-- Code generation sequences
CREATE SEQUENCE code_seq_st START WITH 1;
CREATE SEQUENCE code_seq_cs START WITH 1;

-- Knowledge table
CREATE TABLE knowledge (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_knowledge_metadata_gin ON knowledge USING GIN (metadata);

-- Templates table
CREATE TABLE templates (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    format VARCHAR(255) NOT NULL,
    content BYTEA NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Card types table
CREATE TABLE card_types (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Card type to template relationship
CREATE TABLE card_type_template_rel (
    id BIGSERIAL PRIMARY KEY,
    card_type_code VARCHAR(20) NOT NULL REFERENCES card_types(code),
    template_code VARCHAR(20) NOT NULL REFERENCES templates(code),
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_card_type_template_rel_card_type ON card_type_template_rel(card_type_code);
CREATE INDEX idx_card_type_template_rel_template ON card_type_template_rel(template_code);

-- Accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Account cards table (user's learning progress)
CREATE TABLE account_cards (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    knowledge_code VARCHAR(20) NOT NULL REFERENCES knowledge(code),
    card_type_code VARCHAR(20) NOT NULL REFERENCES card_types(code),
    ease_factor DECIMAL(5,2) NOT NULL DEFAULT 2.5,
    interval_days INTEGER NOT NULL DEFAULT 1,
    repetitions INTEGER NOT NULL DEFAULT 0,
    next_review_date TIMESTAMPTZ NOT NULL,
    last_reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT unique_account_knowledge_card_type UNIQUE (account_id, knowledge_code, card_type_code)
);

CREATE INDEX idx_account_cards_account ON account_cards(account_id);
CREATE INDEX idx_account_cards_next_review ON account_cards(next_review_date);
CREATE INDEX idx_account_cards_knowledge ON account_cards(knowledge_code);
CREATE INDEX idx_account_cards_card_type ON account_cards(card_type_code);

-- Review history table
CREATE TABLE review_history (
    id BIGSERIAL PRIMARY KEY,
    account_card_id BIGINT NOT NULL REFERENCES account_cards(id),
    quality INTEGER NOT NULL,
    reviewed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT valid_quality CHECK (quality >= 0 AND quality <= 5)
);

CREATE INDEX idx_review_history_account_card ON review_history(account_card_id);
CREATE INDEX idx_review_history_reviewed_at ON review_history(reviewed_at);

-- Change requests table
CREATE TABLE change_requests (
    id BIGSERIAL PRIMARY KEY,
    request_type VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE
    target_code VARCHAR(20), -- Code of item being modified (NULL for CREATE)
    payload JSONB NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, APPROVED, REJECTED
    submitter_id BIGINT NOT NULL REFERENCES accounts(id),
    reviewer_id BIGINT REFERENCES accounts(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_change_requests_status ON change_requests(status);
CREATE INDEX idx_change_requests_submitter ON change_requests(submitter_id);
