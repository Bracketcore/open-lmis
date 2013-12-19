/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class FacilityDistributionDTO {

  private FacilityVisit facilityVisit;
  private EpiUseDTO epiUse;

  public FacilityDistribution transform() {
    return new FacilityDistribution(this.facilityVisit, this.epiUse.transform(), null);
  }

  public void setDistributionId(Long distributionId) {
    facilityVisit.setDistributionId(distributionId);
    epiUse.setFacilityId(distributionId);
  }

  public void setFacilityId(Long facilityId) {
    facilityVisit.setFacilityId(facilityId);
    epiUse.setFacilityId(facilityId);
  }

  public void setModifiedBy(Long modifiedBy) {
    facilityVisit.setCreatedBy(modifiedBy);
    epiUse.setModifiedBy(modifiedBy);
  }

}
