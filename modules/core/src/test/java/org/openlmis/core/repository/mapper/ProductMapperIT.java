/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProductMapperIT {

  public static final String PRODUCT_DOSAGE_UNIT_MG = "mg";
  private static final String PRODUCT_FORM_TABLET = "Tablet";

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  @Autowired
  ProgramProductMapper programProductMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  ProductMapper productMapper;

  @Test
  public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
    expectedEx.expect(DataIntegrityViolationException.class);
    expectedEx.expectMessage("null value in column \"primaryname\" violates not-null constraint");
    Product product = new Product();
    product.setCode("ABCD123");
    int status = productMapper.insert(product);
    assertEquals(0, status);
  }

  @Test
  public void shouldReturnDosageUnitIdForCode() {
    Long id = productMapper.getDosageUnitIdForCode(PRODUCT_DOSAGE_UNIT_MG);
    assertThat(id, CoreMatchers.is(1L));

    id = productMapper.getDosageUnitIdForCode("invalid dosage unit");
    assertThat(id, CoreMatchers.is(nullValue()));
  }

  @Test
  public void shouldReturnProductFormIdForCode() {
    Long id = productMapper.getProductFormIdForCode(PRODUCT_FORM_TABLET);
    assertThat(id, CoreMatchers.is(1L));

    id = productMapper.getProductFormIdForCode("invalid product form");
    assertThat(id, CoreMatchers.is(nullValue()));
  }

  @Test
  public void shouldReturnNullForInvalidProductCode() {
    String code = "invalid_code";
    Long productId = productMapper.getIdByCode(code);
    assertThat(productId, is(nullValue()));
  }

  @Test
  public void shouldReturnProductIdForValidProductCode() {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);
    Long id = productMapper.getIdByCode(product.getCode());
    assertThat(id, is(product.getId()));
  }

  @Test
  public void shouldReturnProductByCode() {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);
    Product expectedProduct = productMapper.getByCode(product.getCode());
    assertThat(expectedProduct.getId(), is(product.getId()));
    assertThat(expectedProduct.getCode(), is(product.getCode()));
    assertThat(expectedProduct.getPrimaryName(), is(product.getPrimaryName()));
  }

  @Test
  public void shouldUpdateProduct() {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    product.setPrimaryName("Updated Name");
    product.setAlternateItemCode("Alternate Code");

    productMapper.update(product);

    Product returnedProduct = productMapper.getByCode(product.getCode());

    assertThat(returnedProduct.getPrimaryName(), is("Updated Name"));
    assertThat(returnedProduct.getAlternateItemCode(), is("Alternate Code"));

  }
}
