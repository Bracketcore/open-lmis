DROP TABLE IF EXISTS program_products;
CREATE TABLE program_products (
    id SERIAL PRIMARY KEY,
    programId INTEGER REFERENCES programs(id) NOT NULL,
    productId INTEGER REFERENCES products(id) NOT NULL,
    dosesPerMonth INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    currentPrice NUMERIC(20,2) DEFAULT 0,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE (productId, programId)
);
