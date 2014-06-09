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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductServiceTest {

  @Mock
  private FacilityApprovedProductRepository facilityApprovedProductRepository;

  @Mock
  private ProgramService programService;

  @Mock
  private ProductService productService;

  @Mock
  private ProgramProductService programProductService;

  @Mock
  private FacilityService facilityService;

  @Rule
  public ExpectedException expectedException = none();

  @InjectMocks
  FacilityApprovedProductService facilityApprovedProductService;

  @Test
  public void shouldSaveFacilityApprovedProduct() throws Exception {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
    Long programId = 45L;
    Long productId = 10L;
    Long programProductId = 100L;
    when(programService.getIdForCode(defaultProgramCode)).thenReturn(programId);
    when(productService.getIdForCode(defaultProductCode)).thenReturn(productId);
    when(programProductService.getIdByProgramIdAndProductId(programId, productId)).thenReturn(100L);
    when(facilityService.getFacilityTypeByCode(facilityTypeApprovedProduct.getFacilityType())).thenReturn(new FacilityType());

    facilityApprovedProductService.save(facilityTypeApprovedProduct);

    verify(programService).getIdForCode(defaultProgramCode);
    verify(productService).getIdForCode(defaultProductCode);
    verify(programProductService).getIdByProgramIdAndProductId(programId, productId);
    verify(facilityApprovedProductRepository).insert(facilityTypeApprovedProduct);

    assertThat(facilityTypeApprovedProduct.getProgramProduct().getProgram().getId(), is(programId));
    assertThat(facilityTypeApprovedProduct.getProgramProduct().getProduct().getId(), is(productId));
    assertThat(facilityTypeApprovedProduct.getProgramProduct().getId(), is(programProductId));
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProgramDoesNotExist() throws Exception {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(programService).getIdForCode(defaultProgramCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityTypeApprovedProduct);
    verify(programService).getIdForCode(defaultProgramCode);

    verify(facilityApprovedProductRepository, never()).insert(facilityTypeApprovedProduct);
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProductDoesNotExist() throws Exception {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(productService).getIdForCode(defaultProductCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityTypeApprovedProduct);

    verify(productService).getIdForCode(defaultProgramCode);
    verify(facilityApprovedProductRepository, never()).insert(facilityTypeApprovedProduct);
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProgramProductDoesNotExist() throws Exception {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));

    Long programId = 1L;
    Long productId = 2L;

    when(programService.getIdForCode(defaultProgramCode)).thenReturn(programId);
    when(productService.getIdForCode(defaultProductCode)).thenReturn(productId);

    doThrow(new DataException("abc")).when(programProductService).getIdByProgramIdAndProductId(programId, productId);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityTypeApprovedProduct);

    verify(programProductService).getIdByProgramIdAndProductId(programId, productId);
    verify(facilityApprovedProductRepository, never()).insert(facilityTypeApprovedProduct);
  }
}
