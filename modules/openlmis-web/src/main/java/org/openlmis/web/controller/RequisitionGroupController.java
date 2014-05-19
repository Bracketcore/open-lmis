/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.web.form.RequisitionGroupFormDTO;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class RequisitionGroupController {

  public static final String SEARCH_PAGE_SIZE = "search.page.size";

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private StaticReferenceDataService staticReferenceDataService;

  @RequestMapping(value = "/requisitionGroups", method = GET)
  public ResponseEntity<OpenLmisResponse> search(@RequestParam(value = "searchParam") String searchParam,
                                                 @RequestParam(value = "columnName") String columnName,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page) {
    Integer pageSize = Integer.parseInt(staticReferenceDataService.getPropertyValue(SEARCH_PAGE_SIZE));
    Pagination pagination = new Pagination(page, pageSize);

    List<RequisitionGroup> requisitionGroupList = requisitionGroupService.search(searchParam, columnName, pagination);
    pagination.setTotalRecords(requisitionGroupService.getTotalRecords(searchParam, columnName));

    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("requisitionGroupList", requisitionGroupList);
    response.getBody().addData("pagination", pagination);
    return response;
  }

  @RequestMapping(value = "/requisitionGroups/{id}", method = GET)
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable(value = "id") Long id) {
    RequisitionGroup requisitionGroup = requisitionGroupService.getBy(id);
    List<RequisitionGroupMember> requisitionGroupMembers = requisitionGroupService.getMembersBy(id);
    RequisitionGroupFormDTO requisitionGroupFormDTO = new RequisitionGroupFormDTO(requisitionGroup, requisitionGroupMembers);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("requisitionGroupData", requisitionGroupFormDTO);
    return response;
  }
}
