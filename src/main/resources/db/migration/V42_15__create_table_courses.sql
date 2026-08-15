CREATE TABLE courses (
                         id          UUID PRIMARY KEY,
                         reference   VARCHAR(50) NOT NULL,
                         title       VARCHAR(200) NOT NULL,
                         credit      INTEGER NOT NULL,
                         CONSTRAINT uk_courses_reference UNIQUE (reference)
);
