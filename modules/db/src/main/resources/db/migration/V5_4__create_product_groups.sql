-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE product_groups (
  id           SERIAL PRIMARY KEY,
  code         VARCHAR(50) NOT NULL UNIQUE,
  name         VARCHAR(250) NOT NULL,
  createdBy    INTEGER,
  createdDate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy   INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_product_groups_lower_code ON product_groups(LOWER(code));