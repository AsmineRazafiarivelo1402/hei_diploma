CREATE TABLE course_speciality (
                                   id              UUID PRIMARY KEY,
                                   courses_id      UUID NOT NULL,
                                   speciality_enum speciality_enum NOT NULL,
                                   semester_id     UUID NOT NULL,
                                   CONSTRAINT fk_course_speciality_courses FOREIGN KEY (courses_id) REFERENCES courses (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_course_speciality_semester FOREIGN KEY (semester_id) REFERENCES semester (id) ON DELETE CASCADE
);

CREATE INDEX idx_course_speciality_courses ON course_speciality (courses_id);
CREATE INDEX idx_course_speciality_semester ON course_speciality (semester_id);
