CREATE TABLE group_exam (
                            id          UUID PRIMARY KEY,
                            group_id    UUID NOT NULL,
                            exam_id     UUID NOT NULL,
                            CONSTRAINT fk_group_exam_group FOREIGN KEY (group_id) REFERENCES group_table (id) ON DELETE CASCADE,
                            CONSTRAINT fk_group_exam_exam FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
                            CONSTRAINT uk_group_exam UNIQUE (group_id, exam_id)
);
