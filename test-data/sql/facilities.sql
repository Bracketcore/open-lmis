DELETE FROM programs_supported;
DELETE FROM facilities;

INSERT INTO facilities
 (code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable) values
('F1756','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),
('F1757','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');

insert into programs_supported(facilityId, programId, active, modifiedBy) VALUES
((SELECT id FROM facilities WHERE code = 'F1756'), 1, true, 'Admin123'),
((SELECT id FROM facilities WHERE code = 'F1756'), 2, true, 'Admin123'),
((SELECT id FROM facilities WHERE code = 'F1757'), 1, true, 'Admin123'),
((SELECT id FROM facilities WHERE code = 'F1757'), 2, true, 'Admin123');