DELETE FROM facility_approved_products;
DELETE FROM program_products;
DELETE FROM products;

INSERT INTO products
(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values
('P10',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),
('P11',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE),
('P12',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    TRUE,      TRUE);

INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES
(1, (SELECT id from products WHERE code = 'P10'), 30, 12.5, true),
(1, (SELECT id from products WHERE code = 'P11'), 30, 50, true),
(1, (SELECT id from products WHERE code = 'P12'), 30, 0, true);

INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES
(1, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P10')), 3),
(1, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P11')), 3),
(1, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P12')), 3),
(2, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P10')), 3),
(2, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P11')), 3),
(2, (SELECT id FROM program_products WHERE programId=1 AND productId=(SELECT id FROM products WHERE  code='P12')), 3);
