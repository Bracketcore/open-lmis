/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(VaccinationChildCoverage.class)
@Category(UnitTests.class)
public class VaccinationChildCoverageTest {

  private Facility facility;
  private FacilityVisit facilityVisit;

  @Before
  public void setUp() {
    facility = mock(Facility.class);
    facilityVisit = mock(FacilityVisit.class);
  }

  @Test
  public void shouldCreateTwelveVaccinationChildCoverageLineItemsAndSevenOpenedVialLineItems() throws Exception {
    VaccinationProduct vaccinationProduct = new VaccinationProduct("bcg", null, null);
    List<VaccinationProduct> vaccinationProducts = asList(vaccinationProduct);
    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();

    ProductVial productVial = new ProductVial("bcg", "BCG");
    List<ProductVial> productVials = asList(productVial);

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, vaccinationProduct, "BCG").thenReturn(
      lineItem);
    whenNew(OpenedVialLineItem.class).withArguments(facilityVisit, facility, productVial, "BCG").thenReturn(
      openedVialLineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility,
      vaccinationProducts, productVials);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertThat(vaccinationChildCoverage.getOpenedVialLineItems().size(), is(7));
  }

  @Test
  public void shouldCreateTwelveChildCoverageLineItemsWithVaccinationAsNullForInvalidVaccinationProduct() throws Exception {
    VaccinationProduct invalidVaccination = new VaccinationProduct();
    invalidVaccination.setVaccination("BCG1234");
    List<VaccinationProduct> vaccinationProducts = asList(invalidVaccination);

    ProductVial productVial = new ProductVial("bcg", "BCG");

    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    lineItem.setVaccination("BCG");

    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, null, "BCG").thenReturn(lineItem);
    whenNew(OpenedVialLineItem.class).withArguments(facilityVisit, facility, productVial, "BCG").thenReturn(
      openedVialLineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility,
      vaccinationProducts, asList(productVial));

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertThat(vaccinationChildCoverage.getOpenedVialLineItems().size(), is(7));
    assertTrue(vaccinationChildCoverage.getChildCoverageLineItems().get(0).getVaccination().equals("BCG"));
    verifyNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, null, "BCG");
  }

  @Test
  public void shouldCreateSevenOpenedVialLineItemsWithProductVialAsNullForInvalidProductVialName() throws Exception {
    VaccinationProduct vaccinationProduct = new VaccinationProduct();
    vaccinationProduct.setVaccination("BCG");
    List<VaccinationProduct> vaccinationProducts = asList(vaccinationProduct);

    ProductVial invalidProductVial = new ProductVial("invalidVial", "BCG");
    List<ProductVial> productVials = asList(invalidProductVial);

    ChildCoverageLineItem lineItem = new ChildCoverageLineItem();
    lineItem.setVaccination("BCG");

    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    openedVialLineItem.setProductVialName("BCG");

    whenNew(ChildCoverageLineItem.class).withArguments(facilityVisit, facility, null, "BCG").thenReturn(lineItem);
    whenNew(OpenedVialLineItem.class).withArguments(facilityVisit, facility, null, "BCG").thenReturn(openedVialLineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility,
      vaccinationProducts, productVials);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertThat(vaccinationChildCoverage.getOpenedVialLineItems().size(), is(7));
    assertTrue(vaccinationChildCoverage.getOpenedVialLineItems().get(0).getProductVialName().equals("BCG"));
    verifyNew(OpenedVialLineItem.class).withArguments(facilityVisit, facility, null, "BCG");
  }

  @Test
  public void shouldCreate12ChildCoverageLineItemsAlthoughMoreThan12VaccinationProductsExist() throws Exception {
    List<VaccinationProduct> vaccinationProducts = new ArrayList<>();
    VaccinationProduct invalidVaccination;
    for (int i = 0; i < 13; i++) {
      invalidVaccination = new VaccinationProduct();
      invalidVaccination.setVaccination("invalid" + i);
      vaccinationProducts.add(invalidVaccination);
    }

    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem();
    whenNew(ChildCoverageLineItem.class).withAnyArguments().thenReturn(childCoverageLineItem);

    List<ProductVial> productVials = emptyList();
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    whenNew(OpenedVialLineItem.class).withAnyArguments().thenReturn(openedVialLineItem);
    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility,
      vaccinationProducts, productVials);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertThat(vaccinationChildCoverage.getOpenedVialLineItems().size(), is(7));
  }

  @Test
  public void shouldCreateOnlySevenOpenedVialLineItemsAlthoughMoreThanSevenProductVialsExist() throws Exception {
    List<ProductVial> productVials = new ArrayList<>();
    ProductVial productVial;
    for (int i = 0; i < 13; i++) {
      productVial = new ProductVial();
      productVial.setVial("invalid" + i);
      productVials.add(productVial);
    }

    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    whenNew(OpenedVialLineItem.class).withAnyArguments().thenReturn(openedVialLineItem);

    List<VaccinationProduct> vaccinationProducts = emptyList();
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem();
    whenNew(ChildCoverageLineItem.class).withAnyArguments().thenReturn(childCoverageLineItem);

    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage(facilityVisit, facility,
      vaccinationProducts, productVials);

    assertThat(vaccinationChildCoverage.getChildCoverageLineItems().size(), is(12));
    assertThat(vaccinationChildCoverage.getOpenedVialLineItems().size(), is(7));
  }
}
