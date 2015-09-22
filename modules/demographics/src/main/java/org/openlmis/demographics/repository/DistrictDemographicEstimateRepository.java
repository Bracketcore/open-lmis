/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.demographics.repository;

import org.openlmis.demographics.domain.DistrictDemographicEstimate;
import org.openlmis.demographics.domain.FacilityDemographicEstimate;
import org.openlmis.demographics.dto.DemographicEstimateLineItem;
import org.openlmis.demographics.repository.mapper.DistrictDemographicEstimateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DistrictDemographicEstimateRepository {

  @Autowired
  DistrictDemographicEstimateMapper mapper;

  public List<DistrictDemographicEstimate> getDistrictEstimate(Integer year, Long districtId, Long programId) {
    return mapper.getEstimatesForDistrict(year, districtId, programId);
  }

  public Integer insert(DistrictDemographicEstimate estimate){
    return mapper.insert(estimate);
  }

  public Integer update(DistrictDemographicEstimate estimate){
    return mapper.update(estimate);
  }

  public List<DemographicEstimateLineItem> getDistrictLineItems(String facilityIds){
    return mapper.getDistrictLineItems(facilityIds);
  }

  public List<FacilityDemographicEstimate> getFacilityEstimateAggregate(Integer year, Long districtId, Long programId) {
    return mapper.getFacilityEstimateAggregate(year, districtId, programId);
  }

  public void finalize(DistrictDemographicEstimate est) {
    mapper.finalize(est);
  }

  public void undoFinalize(DistrictDemographicEstimate est) {
    mapper.undoFinalize(est);
  }
}
