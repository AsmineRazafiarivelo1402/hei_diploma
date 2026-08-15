CREATE TABLE users (
                       id              UUID PRIMARY KEY,
                       user_type       VARCHAR(31) NOT NULL,
                       reference       VARCHAR(50) NOT NULL,
                       first_name      VARCHAR(100) NOT NULL,
                       last_name       VARCHAR(100) NOT NULL,
                       birthdate       TIMESTAMP,
                       email           VARCHAR(150) NOT NULL,
                       password        VARCHAR(255) NOT NULL,
                       address         VARCHAR(255),
                       phone_number    VARCHAR(30),
                       role            role_enum NOT NULL,
                       CONSTRAINT uk_users_reference UNIQUE (reference),
                       CONSTRAINT uk_users_email UNIQUE (email)
);
