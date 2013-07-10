/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.repository.DistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributionService {

  @Autowired
  DistributionRepository repository;

  public Distribution create(Distribution distribution) {
    return repository.create(distribution);
  }

  public Distribution get(Distribution distribution) {
    return repository.get(distribution);
  }
}
