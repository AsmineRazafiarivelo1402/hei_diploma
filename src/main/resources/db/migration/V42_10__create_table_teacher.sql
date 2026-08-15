CREATE TABLE teacher (
                         id  UUID PRIMARY KEY,
                         CONSTRAINT fk_teacher_users FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
