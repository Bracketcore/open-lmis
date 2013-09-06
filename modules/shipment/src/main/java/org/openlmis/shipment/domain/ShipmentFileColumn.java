/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class ShipmentFileColumn extends BaseModel {

  private String name;
  private String dataFieldLabel;
  private Integer position;
  private Boolean include;
  private Boolean mandatory;
  private String datePattern;

  public Boolean isIncluded() {
    return include;
  }

  public Boolean isMandatory() {
    return mandatory;
  }

}
