/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.Rnr;
import java.util.Date;
import static java.lang.Boolean.*;

@Data
@NoArgsConstructor
public class Order {
  public Order(Rnr requisition) {
    this.rnrId = requisition.getId();
    this.createdBy = requisition.getModifiedBy();
    this.fulfilled = FALSE;
  }

  private Integer id;
  private Integer rnrId;
  private Boolean fulfilled;
  private Integer shipmentId;
  private Date createdDate;
  private Integer createdBy;
}
