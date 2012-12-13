package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRightsMapper {

  @Insert("INSERT INTO role_rights(role_id, right_id) VALUES " +
      "(#{roleId}, #{right})")
  int createRoleRight(@Param(value = "roleId") Long roleId, @Param(value = "right") Right right);

  @Insert("INSERT INTO roles" +
      "(name, description) VALUES " +
      "(#{name}, #{description})")
  @Options(useGeneratedKeys = true)
  int insertRole(Role role);

  @Select("SELECT RA.user_id, RA.role_id, RA.program_id FROM " +
      "role_assignments RA, users U, role_rights RR WHERE " +
      "U.user_name = #{userName} " +
      "AND U.id  = RA.user_id " +
      "AND RA.role_id = RR.role_id " +
      "AND RR.right_id = #{right} ")
  @Results(value = {
      @Result(property = "userId", column = "user_id"),
      @Result(property = "roleId", column = "role_id"),
      @Result(property = "programId", column = "program_id")})
  List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") Right right,
                                                                @Param(value = "userName") String userName);

  @Insert("INSERT INTO role_assignments" +
      "(user_id, role_id, program_id) VALUES " +
      "(#{user.id}, #{role.id}, #{program.id})")
  int createRoleAssignment(@Param(value = "user") User user,
                           @Param(value = "role") Role role,
                           @Param(value = "program") Program program);

  @Select("SELECT R.id, R.description FROM " +
      "role_assignments RA, users U, role_rights RR, rights R WHERE " +
      "U.user_name = #{userName} " +
      "AND U.id  = RA.user_id " +
      "AND RA.role_id = RR.role_id " +
      "AND R.id= RR.right_id")

  List<Right> getAllRightsForUser(String username);

}
