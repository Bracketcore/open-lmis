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


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductGroupMapperIT {

  @Autowired
  ProductGroupMapper mapper;

  ProductGroup productGroup;

  @Before
  public void setUp() throws Exception {
    productGroup = new ProductGroup();
    productGroup.setCode("PG1");
    productGroup.setName("Vaccines");
  }

  @Test
  public void shouldInsertProductGroup() throws Exception {
    mapper.insert(productGroup);

    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Vaccines"));
  }

  @Test
  public void shouldUpdateProductGroup() throws Exception {
    mapper.insert(productGroup);

    productGroup.setName("Medicines");
    mapper.update(productGroup);
    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Medicines"));
  }

  @Test
  public void shouldGetProductGroupByCode() throws Exception {
    mapper.insert(productGroup);

    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Vaccines"));
  }

  @Test
  public void shouldGetProductGroupById() throws Exception {
    mapper.insert(productGroup);

    ProductGroup returnedProductGroup = mapper.getById(productGroup.getId());

    assertThat(returnedProductGroup.getName(), is("Vaccines"));
  }

  @Test
  public void shouldGetAllProductGroups() throws Exception {
    List<ProductGroup> result = mapper.getAll();

    assertThat(result.size(), is(1));
  }
}
