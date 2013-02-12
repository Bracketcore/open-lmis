package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAssignmentMapper {

  @Select({"SELECT RA.*",
      "FROM role_assignments RA, role_rights RR, supervisory_nodes SN WHERE",
      "RA.supervisoryNodeId = SN.id",
      "AND RA.roleId = RR.roleId",
      "AND RA.userId = #{userId}",
      "AND RR.rightName = #{right}"})
  @Results(value = {@Result(property = "supervisoryNode", column = "supervisoryNodeId", javaType = SupervisoryNode.class,
      one = @One(select = "org.openlmis.core.repository.mapper.SupervisoryNodeMapper.getSupervisoryNode"))})
  List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") Right right,
                                                                @Param(value = "userId") int userId);

  @Insert("INSERT INTO role_assignments" +
      "(userId, roleId, programId, supervisoryNodeId) VALUES " +
      "(#{userId}, #{roleId}, #{programId}, #{supervisoryNodeId})")
  int insertRoleAssignment(@Param(value = "userId") Integer userId,
                           @Param(value = "programId") Integer programId, @Param(value = "supervisoryNodeId") Integer supervisoryNodeId, @Param(value = "roleId") Integer roleId);

  @Delete("DELETE FROM role_assignments WHERE userId=#{id}")
  void deleteAllRoleAssignmentsForUser(int userId);

  @Select("SELECT userId, programId, supervisoryNodeId, array_agg(roleId) as roleIdsAsString " +
      "FROM role_assignments " +
      "WHERE userId=#{userId} AND programId IS NOT NULL AND supervisoryNodeId IS NOT NULL " +
      "GROUP BY userId, programId, supervisoryNodeId ")
  @Results(value = {@Result(property = "supervisoryNode.id", column = "supervisoryNodeId")})
  List<RoleAssignment> getSupervisorRoles(Integer userId);

  @Select("SELECT userId, programId, array_agg(roleId) as roleIdsAsString " +
      "FROM role_assignments " +
      "WHERE userId=#{userId} AND programId IS NOT NULL AND supervisoryNodeId IS NULL " +
      "GROUP BY userId, programId")
  List<RoleAssignment> getHomeFacilityRoles(Integer userId);
}
