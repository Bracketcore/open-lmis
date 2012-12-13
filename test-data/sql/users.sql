INSERT INTO roles
 (id, name, description) VALUES
 (1, 'store in-charge', ''),
 (2, 'district pharmacist', '');

INSERT INTO role_rights
  (role_id, right_id) VALUES
  (1, 'VIEW_REQUISITION'),
  (1, 'CREATE_REQUISITION'),
  (2, 'VIEW_REQUISITION'),
  (2, 'UPLOADS'),
  (2, 'MANAGE_FACILITY'),
  (2, 'CONFIGURE_RNR');

INSERT INTO users
  (id, user_name, password, role, facility_id) VALUES
  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', null);

INSERT INTO role_assignments
  (user_id, role_id, program_id) VALUES
  (100, 2, 1),
  (200, 1, 1);