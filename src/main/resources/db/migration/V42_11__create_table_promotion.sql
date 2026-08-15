CREATE TABLE promotion (
                           id          UUID PRIMARY KEY,
                           start_year  INTEGER NOT NULL,
                           end_year    INTEGER NOT NULL,
                           CONSTRAINT uk_promotion_years UNIQUE (start_year, end_year)
);
