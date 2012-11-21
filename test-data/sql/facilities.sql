delete from programs_supported;
delete from facility;

insert into facility (code,name,description,gln,main_phone,fax,address1,address2,geographic_zone_id,type,catchment_population,latitude,longitude,altitude,operated_by,cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp, has_electricity, is_online, has_electronic_scc, has_electronic_dar,is_active, go_live_date, go_down_date,is_satellite, comment, do_not_display) values
('F1756','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,'warehouse',333,22.1,1.2,3.3,'NGO',9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),
('F1757','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,'lvl3_hospital',333,22.3,1.2,3.3,'FBO',9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');

insert into programs_supported(facility_code, program_code, active, modified_by) values
('F1756', 'HIV', true, 'Admin123'),
('F1756', 'ARV', true, 'Admin123'),
('F1757', 'HIV', true, 'Admin123'),
('F1757', 'ARV', true, 'Admin123');