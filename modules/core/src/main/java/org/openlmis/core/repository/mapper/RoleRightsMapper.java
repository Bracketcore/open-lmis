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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RightType;
import org.openlmis.core.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * RoleRightsMapper maps the roles to rights entity to corresponding representation in database.
 */
@Repository
public interface RoleRightsMapper {

  @Insert("INSERT INTO role_rights(roleId, rightName, createdBy) VALUES " +
    "(#{role.id}, #{right}, #{role.modifiedBy})")
  int createRoleRight(@Param(value = "role") Role role, @Param(value = "right") Right right);

  //used below
  @SuppressWarnings("unused")
  @Select("SELECT rightName FROM role_rights RR WHERE roleId = #{roleId}")
  Set<Right> getAllRightsForRole(Long roleId);

  @Insert({"INSERT INTO roles",
    "(name, description, createdBy,modifiedBy,createdDate,modifiedDate) VALUES",
    "(#{name}, #{description}, #{createdBy},#{modifiedBy},COALESCE(#{createdDate}, NOW()) ," +
      "COALESCE(#{modifiedDate}, NOW()) )"})
  @Options(useGeneratedKeys = true)
  int insertRole(Role role);

  @Select("SELECT * FROM roles WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "rights", javaType = Set.class, column = "id",
      many = @Many(select = "getAllRightsForRole"))
  })
  Role getRole(Long id);

  @Select("SELECT * FROM roles ORDER BY id")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "rights", javaType = Set.class, column = "id",
      many = @Many(select = "getAllRightsForRole"))
  })
  List<Role> getAllRoles();

  @Update("UPDATE roles SET name=#{name}, description=#{description}, modifiedBy=#{modifiedBy}, modifiedDate= DEFAULT WHERE id=#{id}")
  void updateRole(Role role);

  @Delete("DELETE FROM role_rights WHERE roleId=#{roleId}")
  int deleteAllRightsForRole(Long roleId);

  @Select({"SELECT DISTINCT(RR.rightName)",
    "FROM (SELECT userId, roleId FROM role_assignments UNION ALL SELECT userId, roleId FROM fulfillment_role_assignments) A",
    "INNER JOIN users U ON A.userId = U.id",
    "INNER JOIN role_rights RR ON A.roleId = RR.roleId",
    "WHERE A.userId = #{userId}"})
  Set<Right> getAllRightsForUserById(@Param("userId") Long userId);

  @Select({"SELECT DISTINCT RR.rightName " +
    "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
    "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId = ANY(#{commaSeparatedSupervisoryNodeIds}::INTEGER[]) AND RA.programId = #{program.id}"})
  List<Right> getRightsForUserOnSupervisoryNodeAndProgram(@Param("userId") Long userId, @Param("commaSeparatedSupervisoryNodeIds") String commaSeparatedSupervisoryNodeIds, @Param("program") Program program);

  @Select({"SELECT DISTINCT RR.rightName " +
    "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
    "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId IS NULL AND RA.programId = #{program.id}"})
  List<Right> getRightsForUserOnHomeFacilityAndProgram(@Param("userId") Long userId, @Param("program") Program program);

  @Select({"SELECT R.rightType from rights R INNER JOIN role_rights RR ON RR.rightName = R.name AND RR.roleId = #{roleId} LIMIT 1"})
  RightType getRightTypeForRoleId(Long roleId);

  @Select({"SELECT DISTINCT RR.rightName FROM role_rights RR INNER JOIN fulfillment_role_assignments FRA ON RR.roleId = FRA.roleId ",
    "WHERE FRA.userId = #{userId} AND FRA.facilityId = #{warehouseId}"})
  Set<Right> getRightsForUserAndWarehouse(@Param("userId") Long userId, @Param("warehouseId") Long warehouseId);

  @Insert({"INSERT INTO rights(name, rightType, createdDate) VALUES ",
    "(#{templateName}, #{rightType}, CURRENT_TIMESTAMP)"})
  void insertRight(@Param(value = "templateName") String templateName,
                   @Param(value = "rightType") RightType rightType);

  @Select({"SELECT COUNT(*) FROM rights r",
    "INNER JOIN role_rights rt ON rt.rightName = r.name",
    "INNER JOIN role_assignments ra ON ra.roleId = rt.roleId WHERE ra.userId = #{userId}",
    "AND r.rightType='REPORTING'"})
  Integer totalReportingRightsFor(Long userId);
}
