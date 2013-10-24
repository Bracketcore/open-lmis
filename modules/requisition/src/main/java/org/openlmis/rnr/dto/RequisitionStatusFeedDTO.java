/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import org.openlmis.core.dto.BaseFeedDTO;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class RequisitionStatusFeedDTO extends BaseFeedDTO {
  protected Long requisitionId;
  protected RnrStatus requisitionStatus;
  protected boolean emergency;
  protected Long startDate;
  protected Long endDate;

  public RequisitionStatusFeedDTO(Rnr rnr) {
    this.requisitionId = rnr.getId();
    this.requisitionStatus = rnr.getStatus();
    this.emergency = rnr.isEmergency();
    // TODO - Send UTC timestamps - open issue
    if (rnr.getPeriod() == null) return;
    this.startDate = rnr.getPeriod().getStartDate().getTime();
    this.endDate = rnr.getPeriod().getEndDate().getTime();
  }
}
