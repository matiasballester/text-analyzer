CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS words (
    id UUID DEFAULT uuid_generate_v4(),
    value VARCHAR(255),
    PRIMARY KEY (id)
);
