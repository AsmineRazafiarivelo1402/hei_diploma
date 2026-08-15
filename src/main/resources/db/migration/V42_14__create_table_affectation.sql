CREATE TABLE affectation (
                             id              UUID PRIMARY KEY,
                             student_id      UUID NOT NULL,
                             group_id        UUID NOT NULL,
                             semester_id     UUID NOT NULL,
                             status          status_affectation_enum NOT NULL,
                             CONSTRAINT fk_affectation_student FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE,
                             CONSTRAINT fk_affectation_group FOREIGN KEY (group_id) REFERENCES group_table (id) ON DELETE CASCADE,
                             CONSTRAINT fk_affectation_semester FOREIGN KEY (semester_id) REFERENCES semester (id) ON DELETE CASCADE,
                             CONSTRAINT uk_affectation_student_semester UNIQUE (student_id, semester_id)
);

CREATE INDEX idx_affectation_group ON affectation (group_id);
CREATE INDEX idx_affectation_semester ON affectation (semester_id);
