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

package org.openlmis.vaccine.service.demographics;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.repository.demographics.FacilityDemographicEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
public class FacilityDemographicEstimateService {

  @Autowired
  DemographicEstimateCategoryService estimateCategoryService;

  @Autowired
  private FacilityDemographicEstimateRepository repository;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private CommaSeparator commaSeparator;


  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  public void save(DemographicEstimateForm estimate){
    for(DemographicEstimateLineItem dto: emptyIfNull(estimate.getEstimateLineItems())){
      for(FacilityDemographicEstimate est: emptyIfNull(dto.getFacilityEstimates())){
        est.setFacilityId(dto.getId());
        if(est.getId() == null){
          repository.insert(est);
        }else{
          repository.update(est);
        }
      }
    }
  }

  private List<FacilityDemographicEstimate> getEmptyEstimateObjects(List<DemographicEstimateCategory> categories, Long facilityId, Long programId, Integer year, Boolean includeDetails){
    List<FacilityDemographicEstimate> result = new ArrayList<>();
    for(DemographicEstimateCategory category: categories){
      FacilityDemographicEstimate estimate = new FacilityDemographicEstimate();
      estimate.setYear(year);
      estimate.setFacilityId(facilityId);
      estimate.setProgramId(programId);
      estimate.setConversionFactor(category.getDefaultConversionFactor());
      estimate.setDemographicEstimateId(category.getId());
      estimate.setValue(0L);
      if(includeDetails){
        estimate.setCategory(category);
      }
      result.add(estimate);
    }
    return result;
  }

  public DemographicEstimateForm getEstimateFor(Long userId, Long programId , Integer year){
    DemographicEstimateForm form = new DemographicEstimateForm();
    List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);

    List<DemographicEstimateLineItem> facilities = repository.getFacilityList(programId, commaSeparator.commaSeparateIds(requisitionGroups));
    for(DemographicEstimateLineItem facility : facilities){
      facility.setFacilityEstimates(repository.getFacilityEstimate(year, facility.getId(),programId));
      if( facility.getFacilityEstimates().size() == 0 ){
        facility.setFacilityEstimates(getEmptyEstimateObjects(categories, facility.getId(), programId, year,false));
      }
      form.getEstimateLineItems().add(facility);
    }

    return form;
  }

  public List<FacilityDemographicEstimate> getEstimateValuesForFacility(Long facilityId, Long programId, Integer year){
    List<FacilityDemographicEstimate> result =  repository.getFacilityEstimate(year, facilityId, programId);
    if(result == null || result.size() == 0){
      Facility facility = facilityService.getById(facilityId);
       ;
      List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
      result = getEmptyEstimateObjects(categories, facility.getId(), programId, year, false);
      for(FacilityDemographicEstimate estimate: result){
        estimate.calculateAndSetValue(facility.getCatchmentPopulation());
      }
    }
    return result;
  }

  public List<FacilityDemographicEstimate> getEstimateValuesForFacilityWithDetails(Long facilityId, Long programId, Integer year){
    List<FacilityDemographicEstimate> result =  repository.getFacilityEstimateWithDetails(year, facilityId, programId);
    if(result == null || result.size() == 0){
      Facility facility = facilityService.getById(facilityId);
      ;
      List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
      result = getEmptyEstimateObjects(categories, facility.getId(), programId, year, true);
      for(FacilityDemographicEstimate estimate: result){
        estimate.calculateAndSetValue(facility.getCatchmentPopulation());
      }
    }
    return result;
  }
}
