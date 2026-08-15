CREATE TABLE note_history (
                              id          UUID PRIMARY KEY,
                              old_value   DOUBLE PRECISION NOT NULL,
                              new_value   DOUBLE PRECISION NOT NULL,
                              update_at   TIMESTAMP NOT NULL,
                              reason      VARCHAR(500),
                              updated_by  UUID NOT NULL,
                              note_id     UUID NOT NULL,
                              CONSTRAINT fk_note_history_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT,
                              CONSTRAINT fk_note_history_note FOREIGN KEY (note_id) REFERENCES note (id) ON DELETE CASCADE
);

CREATE INDEX idx_note_history_note ON note_history (note_id);
