CREATE TABLE customer
(
    id              VARCHAR(36)     NOT NULL,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    phone_number    VARCHAR(50)     NOT NULL,
    street          VARCHAR(255)    NOT NULL,
    house_number    VARCHAR(20)     NOT NULL,
    postal_code     VARCHAR(20)     NOT NULL,
    city            VARCHAR(100)    NOT NULL,

    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uq_customer_email UNIQUE (email)
);

CREATE TABLE item
(
    id              VARCHAR(36)     NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     VARCHAR(1000)   NOT NULL,
    price           NUMERIC(10, 2)  NOT NULL,
    amount          INTEGER         NOT NULL,

    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT ck_item_price_not_negative CHECK (price >= 0)
);

CREATE TABLE eurder
(
    id              VARCHAR(36)     NOT NULL,
    customer_id     VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_eurder PRIMARY KEY (id),
    CONSTRAINT fk_eurder_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE item_group
(
    eurder_id       VARCHAR(36)     NOT NULL,
    item_id         VARCHAR(36)     NOT NULL,
    item_name       VARCHAR(255)    NOT NULL,
    unit_price      NUMERIC(10, 2)  NOT NULL,
    amount          INTEGER         NOT NULL,
    shipping_date   DATE            NOT NULL,

    CONSTRAINT fk_item_group_eurder FOREIGN KEY (eurder_id) REFERENCES eurder (id),
    CONSTRAINT fk_item_group_item FOREIGN KEY (item_id) REFERENCES item (id)
);

CREATE TABLE app_user
(
    email           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    role            VARCHAR(20)     NOT NULL,
    customer_id     VARCHAR(36),

    CONSTRAINT pk_app_user PRIMARY KEY (email),
    CONSTRAINT fk_app_user_customer FOREIGN KEY (customer_id) REFERENCES customer (id),
    CONSTRAINT ck_app_user_role CHECK (role IN ('ADMIN', 'CUSTOMER'))
);

CREATE INDEX ix_eurder_customer_id ON eurder (customer_id);
CREATE INDEX ix_item_group_eurder_id ON item_group (eurder_id);
CREATE INDEX ix_item_group_shipping_date ON item_group (shipping_date);