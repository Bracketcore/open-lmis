DROP TABLE IF EXISTS requisition;
CREATE TABLE requisition (
  id SERIAL PRIMARY KEY,
  facility_id INTEGER NOT NULL REFERENCES facility(id),
  program_code VARCHAR(50) NOT  NULL REFERENCES program(code),
  status VARCHAR(20) NOT NULL,
  modified_by VARCHAR(50),
  modified_date TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP
);