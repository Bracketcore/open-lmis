/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.repository.mapper.DistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DistributionRepository {

  @Autowired
  DistributionMapper mapper;


  public void create(Distribution distribution) {
    mapper.insert(distribution);
  }
}
