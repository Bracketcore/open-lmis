/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.repository.ProductGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ProductGroupService {

  private ProductGroupRepository productGroupRepository;

  @Autowired
  public ProductGroupService(ProductGroupRepository productGroupRepository) {
      this.productGroupRepository = productGroupRepository;
  }

  public void save(ProductGroup productGroup) {
    if(productGroup.getId() == null) {
      productGroupRepository.insert(productGroup);
    }
    productGroupRepository.update(productGroup);
  }

  public ProductGroup getByCode(String code) {
    return productGroupRepository.getByCode(code);
  }

}
