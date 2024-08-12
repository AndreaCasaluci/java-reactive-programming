DROP TABLE IF EXISTS satellite CASCADE;

CREATE TABLE satellite (
                           id SERIAL PRIMARY KEY,
                           guid UUID NOT NULL DEFAULT gen_random_uuid(),
                           ext_id INT UNIQUE,
                           name VARCHAR(255),
                           date TIMESTAMP WITH TIME ZONE,
                           line1 VARCHAR(255),
                           line2 VARCHAR(255)
);
