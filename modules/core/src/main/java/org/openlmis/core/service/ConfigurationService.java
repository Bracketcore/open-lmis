/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class ConfigurationService {

  private ConfigurationRepository configurationRepository;


  @Autowired
  public ConfigurationService(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  public Configuration getByKey(String key){
      return configurationRepository.getByKey(key);
  }

}
