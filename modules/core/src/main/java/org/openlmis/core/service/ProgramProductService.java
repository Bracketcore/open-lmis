/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProgramProductService {

  public static final String PROGRAM_PRODUCT_INVALID = "programProduct.product.program.invalid";
  private ProgramProductRepository programProductRepository;
  private ProgramRepository programRepository;
  private ProductRepository productRepository;

  @Autowired
  public ProgramProductService(ProgramProductRepository programProductRepository, ProgramRepository programRepository, ProductRepository productRepository) {
    this.programProductRepository = programProductRepository;
    this.programRepository = programRepository;
    this.productRepository = productRepository;
  }

  public Long getIdByProgramIdAndProductId(Long programId, Long productId) {
    return programProductRepository.getIdByProgramIdAndProductId(programId, productId);
  }

  public void updateProgramProductPrice(ProgramProductPrice programProductPrice) {
    programProductPrice.validate();

    ProgramProduct programProduct = programProductPrice.getProgramProduct();
    ProgramProduct programProductWithId = programProductRepository.getByProgramAndProductCode(programProduct);
    if (programProductWithId == null)
      throw new DataException(PROGRAM_PRODUCT_INVALID);

    programProduct.setId(programProductWithId.getId());
    programProduct.setModifiedBy(programProductPrice.getModifiedBy());
    programProduct.setModifiedDate(programProductPrice.getModifiedDate());

    programProductRepository.updateCurrentPrice(programProduct);
    programProductRepository.updatePriceHistory(programProductPrice);
  }

  public void save(ProgramProduct programProduct) {
    programProductRepository.save(programProduct);
  }

  public ProgramProduct getByProgramAndProductCode(ProgramProduct programProduct) {
    return programProductRepository.getByProgramAndProductCode(programProduct);
  }

  public ProgramProductPrice getProgramProductPrice(ProgramProduct programProduct) {
    populateProgramProductIds(programProduct);
    return programProductRepository.getProgramProductPrice(programProduct);
  }

  private void populateProgramProductIds(ProgramProduct programProduct) {
    Long programId = programRepository.getIdByCode(programProduct.getProgram().getCode());
    Long productId = productRepository.getIdByCode(programProduct.getProduct().getCode());
    programProduct.setId(programProductRepository.getIdByProgramIdAndProductId(programId, productId));
  }

  public List<ProgramProduct> getByProgram(Program program) {
    return programProductRepository.getByProgram(program);
  }
}
