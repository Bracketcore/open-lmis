DROP TABLE IF EXISTS facility_approved_products;
CREATE TABLE facility_approved_products (
    id SERIAL PRIMARY KEY,
    facilityTypeId INTEGER REFERENCES facility_type(id),
    productId INTEGER REFERENCES product(id),
    modifiedBy varchar(50),
    modifiedDate TIMESTAMP,
    UNIQUE (facilityTypeId, productId)
);
