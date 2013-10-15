/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
@NoArgsConstructor
public class RoleAssignmentService {

  private RoleAssignmentRepository roleAssignmentRepository;


  @Autowired
  public RoleAssignmentService(RoleAssignmentRepository roleAssignmentRepository) {
    this.roleAssignmentRepository = roleAssignmentRepository;
  }

  /**
   * @deprecated not used in production code
   * @param id
   */
  public void deleteAllRoleAssignmentsForUser(Long id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Long userId) {
    return roleAssignmentRepository.getHomeFacilityRoles(userId);
  }

  public RoleAssignment getAdminRole(Long userId) {
    return roleAssignmentRepository.getAdminRole(userId);
  }

  public RoleAssignment getReportRole(Long userId) {
    return roleAssignmentRepository.getReportRole(userId);
  }

  public List<RoleAssignment> getSupervisorRoles(Long userId) {
    return roleAssignmentRepository.getSupervisorRoles(userId);
  }

  public List<RoleAssignment> getAllocationRoles(Long userId) {
    return roleAssignmentRepository.getAllocationRoles(userId);
  }

  public List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(Long userId, Long programId, Right... rights) {
    return roleAssignmentRepository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, rights);
  }


  public List<RoleAssignment> getRoleAssignments(Right right, Long userId) {
    return roleAssignmentRepository.getRoleAssignmentsForUserWithRight(right, userId);
  }

  public void saveRolesForUser(User user) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(user.getId());
    roleAssignmentRepository.insert(user.getHomeFacilityRoles(), user.getId());
    roleAssignmentRepository.insert(user.getSupervisorRoles(), user.getId());
    roleAssignmentRepository.insert(user.getAllocationRoles(), user.getId());
    roleAssignmentRepository.insert(asList(user.getReportRoles()), user.getId());
    roleAssignmentRepository.insert(asList(user.getAdminRole()), user.getId());
  }
}
