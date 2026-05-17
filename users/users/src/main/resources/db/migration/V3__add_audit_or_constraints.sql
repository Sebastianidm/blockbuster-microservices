ALTER TABLE roles
    ADD CONSTRAINT chk_roles_name_not_blank
    CHECK (TRIM(name) <> '');

ALTER TABLE users
    ADD CONSTRAINT chk_users_username_not_blank
    CHECK (TRIM(username) <> '');

ALTER TABLE users
    ADD CONSTRAINT chk_users_email_not_blank
    CHECK (TRIM(email) <> '');

ALTER TABLE users
    ADD CONSTRAINT chk_users_password_bcrypt_length
    CHECK (CHAR_LENGTH(password) >= 60);
