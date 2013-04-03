-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE requisition_groups (
  id serial PRIMARY KEY,
  code varchar(50) NOT NULL UNIQUE,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  supervisoryNodeId INTEGER,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX ucRequisitionGroupCode ON requisition_groups(LOWER(code));