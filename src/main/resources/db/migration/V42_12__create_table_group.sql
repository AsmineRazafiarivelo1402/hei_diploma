CREATE TABLE group_table (
                             id              UUID PRIMARY KEY,
                             reference       VARCHAR(50) NOT NULL,
                             speciality      speciality_enum NOT NULL,
                             promotion_id    UUID NOT NULL,
                             CONSTRAINT fk_group_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id) ON DELETE CASCADE
);

CREATE INDEX idx_group_promotion ON group_table (promotion_id);
