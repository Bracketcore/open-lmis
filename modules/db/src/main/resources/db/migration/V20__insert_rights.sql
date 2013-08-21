-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO rights (name, rightType, description) VALUES
('UPLOADS', 'ADMIN', 'Permission to upload'),
('UPLOAD_REPORT', 'ADMIN', 'Permission to upload reports'),
('MANAGE_FACILITY', 'ADMIN', 'Permission to manage facilities(crud)'),
('MANAGE_ROLE', 'ADMIN', 'Permission to create and edit roles in the system'),
('MANAGE_SCHEDULE', 'ADMIN', 'Permission to create and edit schedules in the system'),
('CONFIGURE_RNR', 'ADMIN', 'Permission to create and edit r&r template for any program'),
('CREATE_REQUISITION', 'REQUISITION', 'Permission to create, edit, submit and recall requisitions'),
('APPROVE_REQUISITION', 'REQUISITION', 'Permission to approve requisitions'),
('AUTHORIZE_REQUISITION', 'REQUISITION', 'Permission to edit, authorize and recall requisitions'),
('MANAGE_USER', 'ADMIN', 'Permission to create and view users'),
('CONVERT_TO_ORDER', 'ADMIN', 'Permission to convert requisitions to order'),
('VIEW_ORDER', 'ADMIN', 'Permission to view orders'),
('VIEW_REQUISITION', 'REQUISITION', 'Permission to view requisition'),
('VIEW_REPORT', 'ADMIN', 'Permission to view reports'),
('MANAGE_REPORT', 'ADMIN', 'Permission to manage reports'),
('MANAGE_PROGRAM_PRODUCT', 'ADMIN', 'Permission to manage program products'),
('MANAGE_DISTRIBUTION', 'ALLOCATION', 'Permission to manage an distribution'),
('CONFIGURE_EDI', 'ADMIN', 'Permission to configure Electronic Data Interchange (EDI)'),
('MANAGE_REGIMEN_TEMPLATE', 'ADMIN', 'Permission to manage a regimen template');
