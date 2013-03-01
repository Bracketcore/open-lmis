DELETE FROM programs_supported;
DELETE FROM facilities;

INSERT INTO facilities
(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable) values
('F10','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),
('F11','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');

INSERT INTO programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES
((SELECT id FROM facilities WHERE code = 'F10'), 1, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F10'), 2, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 1, '11/11/12', true, 1),
((SELECT id FROM facilities WHERE code = 'F11'), 2, '11/11/12', true, 1);
