CREATE TABLE exam (
                      id              UUID PRIMARY KEY,
                      date            TIMESTAMP NOT NULL,
                      coefficient     NUMERIC(4,2) NOT NULL,
                      exam_type       exam_type_enum NOT NULL,
                      courses_id      UUID NOT NULL,
                      semester_id     UUID NOT NULL,
                      CONSTRAINT fk_exam_courses FOREIGN KEY (courses_id) REFERENCES courses (id) ON DELETE CASCADE,
                      CONSTRAINT fk_exam_semester FOREIGN KEY (semester_id) REFERENCES semester (id) ON DELETE CASCADE
);

CREATE INDEX idx_exam_courses ON exam (courses_id);
CREATE INDEX idx_exam_semester ON exam (semester_id);
