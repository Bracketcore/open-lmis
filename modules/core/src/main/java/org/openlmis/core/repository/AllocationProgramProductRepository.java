/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.openlmis.core.domain.AllocationProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductIsaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllocationProgramProductRepository {

  @Autowired
  ProgramProductIsaMapper programProductIsaMapper;

  @Autowired
  FacilityProgramProductMapper mapper;

  public void insertISA(ProgramProductISA programProductISA) {
    programProductIsaMapper.insert(programProductISA);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    programProductIsaMapper.update(programProductISA);
  }

  public ProgramProductISA getIsaByProgramProductId(Long programProductId) {
    return programProductIsaMapper.getIsaByProgramProductId(programProductId);
  }

  public Integer getOverriddenIsa(Long programProductId, Long facilityId) {
    return mapper.getOverriddenIsa(programProductId, facilityId);
  }

  public void save(AllocationProgramProduct product) {
    mapper.removeFacilityProgramProductMapping(product.getId(), product.getFacilityId());
    mapper.insert(product);
  }

  public List<AllocationProgramProduct> getByFacilityAndProgram(Long facilityId, Long programId) {
    return mapper.getByFacilityAndProgram(facilityId, programId);
  }
}
