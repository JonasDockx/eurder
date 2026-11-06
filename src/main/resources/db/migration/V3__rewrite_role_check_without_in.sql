ALTER TABLE app_user DROP CONSTRAINT ck_app_user_role;

CREATE TABLE user_role
(
    name VARCHAR(20) NOT NULL,

    CONSTRAINT pk_user_role PRIMARY KEY (name)
);

INSERT INTO user_role (name)
VALUES ('ADMIN'),
       ('CUSTOMER');

ALTER TABLE app_user
    ADD CONSTRAINT fk_app_user_role FOREIGN KEY (role) REFERENCES user_role (name);