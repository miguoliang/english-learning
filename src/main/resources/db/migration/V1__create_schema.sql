-- Create sequences for code generation
CREATE SEQUENCE code_seq_st START WITH 1;
CREATE SEQUENCE code_seq_cs START WITH 1;

-- Knowledge table
CREATE TABLE knowledge (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create GIN index on metadata for efficient querying
CREATE INDEX idx_knowledge_metadata_gin ON knowledge USING GIN (metadata);

-- Knowledge relationship table (self-referential many-to-many)
CREATE TABLE knowledge_rel (
    id BIGSERIAL PRIMARY KEY,
    source_knowledge_code VARCHAR(20) NOT NULL REFERENCES knowledge(code),
    target_knowledge_code VARCHAR(20) NOT NULL REFERENCES knowledge(code),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT no_self_ref CHECK (source_knowledge_code != target_knowledge_code),
    CONSTRAINT unique_knowledge_rel UNIQUE (source_knowledge_code, target_knowledge_code)
);

-- Templates table
CREATE TABLE templates (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    format VARCHAR(255),
    content BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Card types table
CREATE TABLE card_types (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Card type template relationship table
CREATE TABLE card_type_template_rel (
    id BIGSERIAL PRIMARY KEY,
    card_type_code VARCHAR(20) NOT NULL REFERENCES card_types(code),
    template_code VARCHAR(20) NOT NULL REFERENCES templates(code),
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT unique_card_type_template_role UNIQUE (card_type_code, template_code, role)
);

-- Translation keys table
CREATE TABLE translation_keys (
    code VARCHAR(20) PRIMARY KEY,
    key VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Translation messages table
CREATE TABLE translation_messages (
    code VARCHAR(20) PRIMARY KEY,
    translation_key_code VARCHAR(20) NOT NULL REFERENCES translation_keys(code),
    locale_code VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT unique_key_locale UNIQUE (translation_key_code, locale_code)
);

-- Accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Account cards table
CREATE TABLE account_cards (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    knowledge_code VARCHAR(20) NOT NULL REFERENCES knowledge(code),
    card_type_code VARCHAR(20) NOT NULL REFERENCES card_types(code),
    ease_factor DECIMAL(5,2) NOT NULL DEFAULT 2.5,
    interval_days INTEGER NOT NULL DEFAULT 1,
    repetitions INTEGER NOT NULL DEFAULT 0,
    next_review_date TIMESTAMP NOT NULL,
    last_reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT unique_account_knowledge_card_type UNIQUE (account_id, knowledge_code, card_type_code)
);

-- Create index on account_cards for efficient due date queries
CREATE INDEX idx_account_cards_due_date ON account_cards(account_id, next_review_date);

-- Review history table
CREATE TABLE review_history (
    id BIGSERIAL PRIMARY KEY,
    account_card_id BIGINT NOT NULL REFERENCES account_cards(id),
    quality INTEGER NOT NULL CHECK (quality >= 0 AND quality <= 5),
    reviewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

-- Create index on review_history for analytics
CREATE INDEX idx_review_history_account_card ON review_history(account_card_id, reviewed_at);

-- Knowledge import sessions table (for Temporal workflow state)
CREATE TABLE knowledge_import_sessions (
    id BIGSERIAL PRIMARY KEY,
    workflow_id VARCHAR(255) NOT NULL UNIQUE,
    csv_data TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'pending_validation',
    validation_results JSONB,
    comparison_results JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

