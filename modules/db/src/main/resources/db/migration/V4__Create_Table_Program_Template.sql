Drop TABLE IF EXISTS program_rnr_template;
CREATE TABLE program_rnr_template(
    id SERIAL PRIMARY KEY ,
    column_id INTEGER NOT NULL REFERENCES master_rnr_template(id),
    program_code VARCHAR(50) NOT NULL,
    label VARCHAR(200) NOT NULL,
    is_visible BOOLEAN NOT NULL,
    position int NOT NULL,
    column_type varchar(50),
    UNIQUE (program_code, column_id)
);