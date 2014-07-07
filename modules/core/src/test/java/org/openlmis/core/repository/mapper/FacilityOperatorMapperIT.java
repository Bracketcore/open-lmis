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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityOperatorMapperIT {

  @Autowired
  private FacilityOperatorMapper mapper;

  @Test
  public void shouldInsertAndUpdateFacilityOperatorByCode() {
    FacilityOperator facOp = new FacilityOperator();
    facOp.setCode("someCode");
    facOp.setText("someText");
    facOp.setDisplayOrder(1);

    // insert
    mapper.insert(facOp);

    // test insert
    FacilityOperator retFacOp = mapper.getByCode(facOp.getCode());
    assertThat(retFacOp, is(facOp));

    // update
    retFacOp.setCode("aNewCode");
    retFacOp.setText("aNewText");
    retFacOp.setDisplayOrder(2);
    mapper.update(retFacOp);

    // test update
    FacilityOperator updatedFacOp = mapper.getByCode(retFacOp.getCode());
    assertThat(updatedFacOp, is(retFacOp));
  }
}
