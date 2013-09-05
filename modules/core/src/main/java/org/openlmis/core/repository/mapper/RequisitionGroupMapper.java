/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMapper {

    @Insert("INSERT INTO requisition_groups" +
            "(code, name, description, supervisoryNodeId, createdBy, modifiedBy, modifiedDate) " +
            "values (#{code}, #{name}, #{description}, #{supervisoryNode.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate}) ")
    @Options(useGeneratedKeys = true)
    Integer insert(RequisitionGroup requisitionGroup);

    @Select("SELECT id, code, name, description, supervisoryNodeId, modifiedBy, modifiedDate " +
            "FROM requisition_groups WHERE id = #{id}")
    @Results(value = {
            @Result(property = "supervisoryNode.id", column = "supervisoryNodeId")
    })
    RequisitionGroup getRequisitionGroupById(Long id);

    @Select("SELECT id FROM requisition_groups where LOWER(code) = LOWER(#{code})")
    Long getIdForCode(String code);

    @Select("SELECT id FROM requisition_groups where supervisoryNodeId = ANY(#{supervisoryNodeIdsAsString}::INTEGER[])")
    List<RequisitionGroup> getRequisitionGroupBySupervisoryNodes(String supervisoryNodeIdsAsString);

  @Select("SELECT * " +
      "FROM requisition_groups rg " +
      "INNER JOIN requisition_group_program_schedules rgps ON rg.id = rgps.requisitionGroupId " +
      "INNER JOIN requisition_group_members rgm ON rgps.requisitionGroupId = rgm.requisitionGroupId " +
      "WHERE rgps.programId = #{program.id} " +
      "AND RGM.facilityId = #{facility.id}")
  RequisitionGroup getRequisitionGroupForProgramAndFacility(@Param(value = "program") Program program, @Param(value = "facility") Facility facility);

  @Select("SELECT * FROM requisition_groups where LOWER(code) = LOWER(#{code})")
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId")
  })
  RequisitionGroup getByCode(String code);

  @Update("UPDATE requisition_groups " +
    "SET name = #{name}, description =  #{description}, supervisoryNodeId = #{supervisoryNode.id}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
    "WHERE id = #{id}")
  void update(RequisitionGroup requisitionGroup);


  @Select("SELECT * " +
          "FROM   (SELECT rg.*,  " +
          "               sn.name AS supervisoryNodeName  " +
          "        FROM   requisition_groups rg  " +
          "               JOIN supervisory_nodes sn  " +
          "                 ON rg.supervisorynodeid = sn.id) AS y  " +
          "       LEFT JOIN (SELECT requisitiongroupid        AS id,  " +
          "                    Count(DISTINCT programid) programCount  " +
          "             FROM   requisition_group_program_schedules rgp  " +
          "             GROUP  BY requisitiongroupid) AS x  " +
          "         ON y.id = x.id  " +
          "       LEFT JOIN (SELECT requisitiongroupid         AS id,  " +
          "                    Count(DISTINCT facilityid) facilityCount  " +
          "             FROM   requisition_group_members rgm  " +
          "             GROUP  BY rgm.requisitiongroupid) AS z  " +
          "         ON z.id = x.id " +
          "ORDER BY y.supervisoryNodeName, y.name")
  @Results(value={
          @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
          @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
          @Result(property = "countOfFacilities", column = "facilityCount"),
          @Result(property = "countOfPrograms", column = "programCount")

  })
  List<RequisitionGroup> getCompleteList();


  @Delete("DELETE FROM requisition_groups WHERE ID = #{id}")
  void removeRequisitionGroup(@Param(value="id") Long id);

}
