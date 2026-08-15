CREATE TABLE student (
                         id  UUID PRIMARY KEY,
                         CONSTRAINT fk_student_users FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
