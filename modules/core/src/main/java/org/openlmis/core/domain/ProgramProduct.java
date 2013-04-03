/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProgramProduct extends BaseModel implements Importable {

  @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
  private Program program;
  @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
  private Product product;
  @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
  private Integer dosesPerMonth;
  @ImportField(name = "Is Active", type = "boolean", mandatory = true)
  private boolean active;

  private Money currentPrice;
  public static final String PROGRAM_PRODUCT_INVALID_CURRENT_PRICE = "programProduct.invalid.current.price";

  public ProgramProduct(Program program, Product product, Integer dosesPerMonth, Boolean active) {
    this.program = program;
    this.product = product;
    this.dosesPerMonth = dosesPerMonth;
    this.active = active;
  }

  public ProgramProduct(Program program, Product product, Integer dosesPerMonth, Boolean active, Money currentPrice) {
    this.program = program;
    this.product = product;
    this.dosesPerMonth = dosesPerMonth;
    this.active = active;
    this.currentPrice = currentPrice;
  }

  public void validate() {
    if(currentPrice.isNegative()) throw new DataException(PROGRAM_PRODUCT_INVALID_CURRENT_PRICE);
  }
}
