-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE supervisory_nodes (
  id serial PRIMARY KEY,
  parentId INT REFERENCES supervisory_nodes(id),
  facilityId INT NOT NULL REFERENCES facilities(id),
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(250),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_supervisory_node_parentId ON supervisory_nodes(parentId);

CREATE UNIQUE INDEX uc_supervisory_nodes_lower_code ON supervisory_nodes(LOWER(code));