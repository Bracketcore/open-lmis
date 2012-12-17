DROP TABLE IF EXISTS program_products;
CREATE TABLE program_products (
    id SERIAL PRIMARY KEY,
    programId INTEGER REFERENCES programs(id) NOT NULL,
    productId INTEGER REFERENCES products(id) NOT NULL,
    dosesPerMonth INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    modifiedBy VARCHAR(50),
    modifiedDate TIMESTAMP,
    UNIQUE (productId, programId)
);
