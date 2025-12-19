-- Create refresh_tokens table in auth schema
CREATE TABLE IF NOT EXISTS auth.refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) 
        REFERENCES auth.user_details(id) ON DELETE CASCADE
);

-- Create index on token for faster lookups
CREATE INDEX idx_refresh_tokens_token ON auth.refresh_tokens(token);

-- Create index on user_id for faster user-based queries
CREATE INDEX idx_refresh_tokens_user_id ON auth.refresh_tokens(user_id);

-- Create index on expiry_date for cleanup queries
CREATE INDEX idx_refresh_tokens_expiry ON auth.refresh_tokens(expiry_date);

-- Add comment to table
COMMENT ON TABLE auth.refresh_tokens IS 'Stores refresh tokens for JWT authentication';

