/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentInventoryMapperIT {

  @Autowired
  EquipmentInventoryMapper mapper;

  @Autowired
  EquipmentTypeMapper typeMapper;

  @Autowired
  EquipmentMapper equipmentMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  QueryExecutor queryExecutor;

  EquipmentInventory inventory;
  Facility facility;
  EquipmentType type;

  @Before
  public void initialize() throws Exception {
    GeographicZone zone = new GeographicZone();
    zone.setId(1L);

    FacilityType facilityType = new FacilityType();
    facilityType.setId(1L);

    facility = new Facility();
    facility.setId(1L);
    facility.setCode("FAC");
    facility.setName("Facility");
    facility.setGeographicZone(zone);
    facility.setFacilityType(facilityType);
    facility.setSdp(true);
    facility.setActive(true);
    facility.setGoLiveDate(new Date());
    facility.setEnabled(true);
    facility.setVirtualFacility(false);
    facilityMapper.insert(facility);

    type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    Equipment equipment = new Equipment();
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipmentMapper.insert(equipment);

    inventory = new EquipmentInventory();
    inventory.setProgramId(1L);
    inventory.setFacilityId(facility.getId());
    inventory.setOperationalStatusId(1L);
    inventory.setNotFunctionalStatusId(2L);
    inventory.setEquipmentId(equipment.getId());
    inventory.setReplacementRecommended(false);
    inventory.setYearOfInstallation(2012);
    inventory.setSerialNumber("2323");
    inventory.setPurchasePrice(0F);
    inventory.setDateLastAssessed(DateTime.now().toDate());
    inventory.setIsActive(true);
    mapper.insert(inventory);
  }

  @Test
  public void shouldGetAllInventoryItemsForFacility() throws Exception{
    List<EquipmentInventory> inventories =  mapper.getInventoryByFacilityAndProgram(facility.getId(), 1L);
    assertEquals(inventories.size(), 1);
  }

  @Test
  public void shouldGetInventory() throws Exception{
    Pagination page1 = new Pagination(1, 2);
    Pagination page2 = new Pagination(2, 2);

    inventory.setSerialNumber("2324");
    mapper.insert(inventory);

    inventory.setSerialNumber("2325");
    mapper.insert(inventory);

    List<EquipmentInventory> inventories =  mapper.getInventory(1L, type.getId(), "{"+facility.getId()+"}", page1);
    assertEquals(inventories.size(), 2);

    List<EquipmentInventory> inventories2 =  mapper.getInventory(1L, type.getId(), "{"+facility.getId()+"}", page2);
    assertEquals(inventories2.size(), 1);
  }

  @Test
  public void shouldGetInventoryById() throws Exception {
    EquipmentInventory result = mapper.getInventoryById(inventory.getId());

    assertEquals(result.getSerialNumber(), inventory.getSerialNumber());
    assertEquals(result.getOperationalStatusId(), inventory.getOperationalStatusId());
  }

  @Test
  public void shouldInsert()throws Exception {
    assertThat(inventory.getId(), CoreMatchers.is(notNullValue()));

    ResultSet rs = queryExecutor.execute("Select * from equipment_inventories where id = " + inventory.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("serialNumber"), "2323");
  }

  @Test
  public void shouldUpdate() throws Exception{
    inventory.setSerialNumber("3432");

    mapper.update(inventory);

    ResultSet rs = queryExecutor.execute("Select * from equipment_inventories where id = " + inventory.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("serialNumber"), "3432");
  }

  @Test
  public void shouldUpdateStatus() throws Exception {
    inventory.setOperationalStatusId(2L);

    mapper.updateStatus(inventory);

    ResultSet rs = queryExecutor.execute("Select * from equipment_inventories where id = " + inventory.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("operationalStatusId"), "2");
  }
}