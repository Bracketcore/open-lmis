-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE facility_types (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(30) NOT NULL UNIQUE,
  description varchar(250) ,
  levelId INTEGER,
  nominalMaxMonth INTEGER NOT NULL,
  nominalEop NUMERIC(4,2) NOT NULL,
  displayOrder INTEGER,
  active BOOLEAN,
  modifiedBy INTEGER,
  lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INTEGER,
  createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);