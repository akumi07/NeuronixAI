CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    file_name VARCHAR(255) NOT NULL,

    file_type VARCHAR(100) NOT NULL,

    file_size BIGINT,

    storage_path VARCHAR(500),

    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documents_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_documents_user_id
    ON documents(user_id);