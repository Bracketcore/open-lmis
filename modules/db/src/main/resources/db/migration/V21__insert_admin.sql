INSERT INTO roles
 (name, description) VALUES
 ('Admin', 'Admin');

 INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name = 'Admin'), 'UPLOADS'),
  ((select id from roles where name = 'Admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'Admin'), 'MANAGE_ROLE'),
  ((select id from roles where name = 'Admin'), 'MANAGE_SCHEDULE'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_RNR');

INSERT INTO users
  (userName, password, facilityId) VALUES
  ('Admin123', 'TQskzK3iLfbRVHeM1muvBCiKribfl6lh8+o91hb74G3OvsybvkzpPI4S3KIeWTXAiwlUU0iSxWi4wSuS8mokSA==', null);

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Admin'));



