CREATE TABLE note (
                      id          UUID PRIMARY KEY,
                      value       DOUBLE PRECISION NOT NULL,
                      date        TIMESTAMP NOT NULL,
                      exam_id     UUID NOT NULL,
                      student_id  UUID NOT NULL,
                      CONSTRAINT fk_note_exam FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
                      CONSTRAINT fk_note_student FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE,
                      CONSTRAINT uk_note_exam_student UNIQUE (exam_id, student_id)
);

CREATE INDEX idx_note_student ON note (student_id);
