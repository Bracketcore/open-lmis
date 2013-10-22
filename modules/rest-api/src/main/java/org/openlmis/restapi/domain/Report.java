/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {
  private Long requisitionId;
  private Long facilityId;
  private Long programId;
  private Long periodId;
  private String userId;
  private Boolean emergency;
  private List<RnrLineItem> products;

  public void validate() {
    if (facilityId == null || programId == null || periodId == null || userId == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }
  }

  public Rnr getRequisition() {
    Rnr rnr = new Rnr();
    rnr.setId(requisitionId);
    rnr.setFullSupplyLineItems(products);
    return rnr;
  }
}