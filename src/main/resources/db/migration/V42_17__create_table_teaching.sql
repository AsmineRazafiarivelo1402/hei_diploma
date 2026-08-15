CREATE TABLE teaching (
                          id          UUID PRIMARY KEY,
                          courses_id  UUID NOT NULL,
                          teacher_id  UUID NOT NULL,
                          CONSTRAINT fk_teaching_courses FOREIGN KEY (courses_id) REFERENCES courses (id) ON DELETE CASCADE,
                          CONSTRAINT fk_teaching_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id) ON DELETE CASCADE,
                          CONSTRAINT uk_teaching_courses_teacher UNIQUE (courses_id, teacher_id)
);
