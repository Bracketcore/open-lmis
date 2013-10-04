/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMemberMapper {

  @Insert("INSERT INTO requisition_group_members" +
    "(requisitionGroupId, facilityId, createdBy, modifiedBy, modifiedDate) " +
    "VALUES (#{requisitionGroup.id}, #{facility.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroupMember requisitionGroupMember);

  @Select({"SELECT rgps.programId FROM requisition_groups rg",
    "INNER JOIN requisition_group_program_schedules rgps ON rg.id = rgps.requisitionGroupId",
    "INNER JOIN requisition_group_members rgm ON rg.id = rgm.requisitionGroupId",
    "WHERE rgm.facilityId = #{facilityId}"})
  List<Long> getRequisitionGroupProgramIdsForFacilityId(Long facilityId);

  @Select({"SELECT *",
    "FROM requisition_group_members",
    "WHERE requisitionGroupId = #{requisitionGroup.id}",
    "AND facilityId = #{facility.id}"})
  RequisitionGroupMember getMappingByRequisitionGroupIdAndFacilityId(
    @Param(value = "requisitionGroup") RequisitionGroup requisitionGroup,
    @Param(value = "facility") Facility facility);

  @Update("UPDATE requisition_group_members " +
    "SET modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE " +
    "requisitionGroupId = #{requisitionGroup.id} AND facilityId = #{facility.id}")
  void update(RequisitionGroupMember requisitionGroupMember);

  @Delete("DELETE from requisition_group_members WHERE " +
          "requisitionGroupId = #{requisitionGroup.id} AND facilityId = #{facility.id}")
  void removeRequisitionGroupMember(
  @Param(value = "requisitionGroup") RequisitionGroup requisitionGroup,
  @Param(value="facility") Facility facility);

}