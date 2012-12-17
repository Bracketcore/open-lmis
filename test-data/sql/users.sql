INSERT INTO roles
 (id, name, description) VALUES
 (2, 'store in-charge', ''),
 (3, 'district pharmacist', '');

INSERT INTO role_rights
  (roleId, rightId) VALUES
  (2, 'VIEW_REQUISITION'),
  (2, 'CREATE_REQUISITION'),
  (3, 'VIEW_REQUISITION'),
  (3, 'UPLOADS'),
  (3, 'MANAGE_FACILITY'),
  (3, 'CONFIGURE_RNR');

INSERT INTO users
  (id, userName, password, role, facilityId) VALUES
  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', 1);

INSERT INTO role_assignments
  (userId, roleId, programId) VALUES
  (200, 1, 2);