CREATE TABLE admin (
                       id  UUID PRIMARY KEY,
                       CONSTRAINT fk_admin_users FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
