CREATE TABLE semester (
                          id              UUID PRIMARY KEY,
                          semester_enum   semester_enum NOT NULL,
                          date_debut      TIMESTAMP NOT NULL,
                          date_fin        TIMESTAMP NOT NULL
);
