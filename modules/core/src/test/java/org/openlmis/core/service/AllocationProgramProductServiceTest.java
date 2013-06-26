/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.AllocationProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.AllocationProgramProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AllocationProgramProductServiceTest {

  @InjectMocks
  private AllocationProgramProductService service;

  @Mock
  private AllocationProgramProductRepository repository;

  @Mock
  ProgramProductService programProductService;

  @Test
  public void shouldInsertIsa() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    service.insertISA(isa);
    verify(repository).insertISA(isa);
  }

  @Test
  public void shouldUpdateIsa() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    service.updateISA(isa);
    verify(repository).updateISA(isa);
  }

  @Test
  public void shouldGetProductsFilledWithIsa() throws Exception {
    final ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1l);
    final ProgramProduct programProduct2 = new ProgramProduct();
    programProduct2.setId(2l);

    List<ProgramProduct> products = new ArrayList<ProgramProduct>() {{
      add(programProduct);
      add(programProduct2);
    }};
    when(programProductService.getByProgram(new Program(1l))).thenReturn(products);

    AllocationProgramProduct allocationProduct1 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(1l);
    when(repository.getByProgramProductId(programProduct.getId())).thenReturn(allocationProduct1);

    AllocationProgramProduct allocationProduct2 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(2l);
    when(repository.getByProgramProductId(programProduct2.getId())).thenReturn(allocationProduct2);

    List<AllocationProgramProduct> returnedProducts = service.get(1l);

    assertThat(returnedProducts.get(0), is(allocationProduct1));
    assertThat(returnedProducts.get(1), is(allocationProduct2));
    verify(programProductService).getByProgram(new Program(1l));
    verify(allocationProduct1).fillFrom(programProduct);
    verify(allocationProduct2).fillFrom(programProduct2);
    verify(repository).getByProgramProductId(programProduct.getId());
    verify(repository).getByProgramProductId(programProduct2.getId());
  }

  @Test
  public void shouldGetProductsFilledWithIsaForAFacility() throws Exception {
    long facilityId = 2l;
    final ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1l);
    final ProgramProduct programProduct2 = new ProgramProduct();
    programProduct2.setId(2l);

    List<ProgramProduct> products = new ArrayList<ProgramProduct>() {{
      add(programProduct);
      add(programProduct2);
    }};
    when(programProductService.getByProgram(new Program(1l))).thenReturn(products);

    AllocationProgramProduct allocationProduct1 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(1l);
    when(repository.getByProgramProductId(programProduct.getId())).thenReturn(allocationProduct1);
    when(repository.getOverriddenIsa(programProduct.getId(), facilityId)).thenReturn(34);

    AllocationProgramProduct allocationProduct2 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(2l);
    when(repository.getByProgramProductId(programProduct2.getId())).thenReturn(allocationProduct2);
    when(repository.getOverriddenIsa(programProduct2.getId(), facilityId)).thenReturn(44);

    List<AllocationProgramProduct> returnedProducts = service.getForProgramAndFacility(1l, facilityId);

    assertThat(returnedProducts.get(0), is(allocationProduct1));
    assertThat(returnedProducts.get(0).getOverriddenIsa(), is(34));

    assertThat(returnedProducts.get(1), is(allocationProduct2));
    assertThat(returnedProducts.get(1).getOverriddenIsa(), is(44));

    verify(programProductService).getByProgram(new Program(1l));
    verify(allocationProduct1).fillFrom(programProduct);
    verify(allocationProduct2).fillFrom(programProduct2);
    verify(repository).getOverriddenIsa(programProduct.getId(), facilityId);
    verify(repository).getOverriddenIsa(programProduct2.getId(), facilityId);
  }

  @Test
  public void shouldSaveAllocationProgramProductList() throws Exception {
    final AllocationProgramProduct allocationProduct1 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(1l);
    final AllocationProgramProduct allocationProduct2 = spy(new AllocationProgramProduct());
    allocationProduct1.setId(2l);
    List<AllocationProgramProduct> allocationProgramProducts = new ArrayList<AllocationProgramProduct>() {{
      add(allocationProduct1);
      add(allocationProduct2);
    }};

    service.saveOverriddenIsa(1l, allocationProgramProducts);

    verify(repository).save(allocationProduct1);
    verify(repository).save(allocationProduct2);
  }
}
