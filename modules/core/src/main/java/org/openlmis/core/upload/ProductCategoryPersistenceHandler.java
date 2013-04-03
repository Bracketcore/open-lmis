/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productCategoryPersistenceHandler")
public class ProductCategoryPersistenceHandler  extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_PRODUCT_CATEGORY = "Duplicate Product Category";
  ProductCategoryService productCategoryService;

  @Autowired
  public ProductCategoryPersistenceHandler(ProductCategoryService productCategoryService) {
    super(DUPLICATE_PRODUCT_CATEGORY);
    this.productCategoryService = productCategoryService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return productCategoryService.getByCode(((ProductCategory) record).getCode());
  }

  @Override
  protected void save(BaseModel modelClass) {
    productCategoryService.save((ProductCategory) modelClass);
  }
}
