-- Add new fields to user_details table
ALTER TABLE auth.user_details
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500);

-- Update existing users to be active
UPDATE auth.user_details SET is_active = TRUE WHERE is_active IS NULL;
UPDATE auth.user_details SET is_verified = FALSE WHERE is_verified IS NULL;

-- Add comments
COMMENT ON COLUMN auth.user_details.is_active IS 'Indicates if user account is active';
COMMENT ON COLUMN auth.user_details.is_verified IS 'Indicates if user email is verified';
COMMENT ON COLUMN auth.user_details.profile_image_url IS 'URL to user profile image';

