CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customers
(
    id        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    firstname VARCHAR(100)        NOT NULL,
    lastname  VARCHAR(100)        NOT NULL,
    email     VARCHAR(150) UNIQUE NOT NULL,
    phone     VARCHAR(50)
);

CREATE TABLE rooms
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_number VARCHAR(20) UNIQUE NOT NULL,
    room_type   VARCHAR(50)        NOT NULL,
    price       NUMERIC(10, 2)     NOT NULL,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES customers (id) ON DELETE CASCADE,
    room_id     UUID NOT NULL REFERENCES rooms (id),
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    note        TEXT,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);
