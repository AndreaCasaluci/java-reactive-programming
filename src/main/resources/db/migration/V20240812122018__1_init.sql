DROP TABLE IF EXISTS satellite CASCADE;

CREATE TABLE satellite (
    id SERIAL PRIMARY KEY,
    guid UUID NOT NULL DEFAULT gen_random_uuid(),
    ext_id INTEGER,
    name VARCHAR(255),
    line1 TEXT,
    line2 TEXT
)