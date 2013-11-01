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
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class GeographicZoneMapperIT {

  @Autowired
  private GeographicZoneMapper mapper;

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name", new GeographicLevel(2L,"state", "State", 2), null);
    geographicZone.setCatchmentPopulation(10000L);
    geographicZone.setLongitude(333.9874);
    geographicZone.setLatitude(-256.7249);
    Date date = new Date();
    geographicZone.setModifiedDate(date);

    mapper.insert(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");

    assertThat(returnedZone, is(geographicZone));
  }

  @Test
  public void shouldGetGeographicLevelByCode() throws Exception {
    String code = "state";
    GeographicLevel geographicLevel = mapper.getGeographicLevelByCode(code);
    assertThat(geographicLevel.getName(), is("State"));
    assertThat(geographicLevel.getId(), is(2L));
  }

  @Test
  public void shouldGetNullIfZoneNotPresent() throws Exception {
    GeographicZone nullZone = mapper.getGeographicZoneByCode("some random code");

    assertThat(nullZone, is(nullValue()));
  }

  @Test
  public void shouldGetAllGeographicZonesOfLowestLevelExceptRootGeographicZoneSortedByName() throws Exception {
    List<GeographicZone> allGeographicZones = mapper.getAllGeographicZones();
    assertThat(allGeographicZones.size(), is(10));
    GeographicZone geographicZone = allGeographicZones.get(0);

    assertThat(geographicZone.getCode(), is("District1"));
    assertThat(geographicZone.getName(), is("District1"));
    assertThat(geographicZone.getLevel().getName(), is("District"));
    assertThat(geographicZone.getLevel().getLevelNumber(), is(4));
  }

  @Test
  public void shouldGetGeographicZoneWithParent() throws Exception {
    GeographicZone parent = new GeographicZone(null, "Dodoma", "Dodoma", new GeographicLevel(null, "province", "Province", null), null);
    GeographicZone expectedZone = new GeographicZone(5L, "Ngorongoro", "Ngorongoro", new GeographicLevel(null, "district", "District", 4), parent);

    GeographicZone zone = mapper.getWithParentById(5L);

    assertThat(zone, is(expectedZone));
  }

  @Test
  public void shouldUpdateGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name", new GeographicLevel(2L,"state", "State", 2), null);
    geographicZone.setLongitude(123.9878);

    mapper.insert(geographicZone);

    geographicZone.setName("new name");
    geographicZone.setLevel(new GeographicLevel(1L,"country", "Country", 1));
    geographicZone.setLongitude(-111.9877);

    mapper.update(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");
    returnedZone.setModifiedDate(null);

    assertThat(returnedZone, is(geographicZone));
  }
}