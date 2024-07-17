CREATE TABLE IF NOT EXISTS files(
    id SERIAL PRIMARY KEY,
    file BYTEA,
    title VARCHAR(100),
    creation_date TIMESTAMP DEFAULT NOW(),
    description VARCHAR(500)
);