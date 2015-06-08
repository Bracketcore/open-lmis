/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Pagination;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.*;
import org.openlmis.equipment.repository.mapper.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentInventoryRepositoryTest {

  @Mock
  private EquipmentInventoryMapper mapper;

  @Mock
  private EquipmentMapper equipmentMapper;

  @Mock
  private EquipmentTypeMapper equipmentTypeMapper;

  @Mock
  private ColdChainEquipmentMapper coldChainEquipmentMapper;

  @Mock
  private EquipmentInventoryStatusMapper equipmentInventoryStatusMapper;

  @InjectMocks
  private EquipmentInventoryRepository repository;

  private long equipmentTypeId = 1L;
  private long equipmentId = 1L;
  private long programId = 1L;
  private long facilityId = 1L;
  private long facilityId2 = 2L;
  private long inventoryId = 1L;
  private long statusId = 1L;
  private long notFunctionalStatusId = 2L;

  private EquipmentType equipmentType;
  private Equipment equipment;
  private ColdChainEquipment coldChainEquipment;
  private EquipmentInventory inventory;
  private EquipmentInventoryStatus status;

  @Before
  public void initialize() throws Exception {
    equipmentType = new EquipmentType();
    equipmentType.setId(equipmentTypeId);

    equipment = new Equipment();
    equipment.setId(equipmentId);
    equipment.setEquipmentTypeId(equipmentTypeId);
    equipment.setEquipmentType(equipmentType);

    String pqsCode = "PQS001";
    coldChainEquipment = new ColdChainEquipment();
    coldChainEquipment.setId(equipmentId);
    coldChainEquipment.setEquipmentTypeId(equipmentTypeId);
    coldChainEquipment.setEquipmentType(equipmentType);
    coldChainEquipment.setPqsCode(pqsCode);

    status = new EquipmentInventoryStatus();
    status.setInventoryId(inventoryId);
    status.setStatusId(statusId);
    status.setNotFunctionalStatusId(notFunctionalStatusId);

    inventory = new EquipmentInventory();
    inventory.setId(inventoryId);
    inventory.setEquipmentId(equipmentId);
    inventory.setOperationalStatusId(statusId);
    inventory.setNotFunctionalStatusId(notFunctionalStatusId);
  }

  @Test
  public void shouldGetFacilityInventory() throws Exception {
    // Set up variables
    equipmentType.setColdChain(false);
    inventory.setEquipment(equipment);
    List<EquipmentInventory> inventories = new ArrayList<>();
    inventories.add(inventory);

    // Set up mock calls
    when(mapper.getInventoryByFacilityAndProgram(facilityId, programId)).thenReturn(inventories);
    when(equipmentMapper.getById(equipmentId)).thenReturn(equipment);
    when(equipmentTypeMapper.getEquipmentTypeById(equipmentTypeId)).thenReturn(equipmentType);
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    List<EquipmentInventory> results = repository.getFacilityInventory(facilityId, programId);

    // Test the results
    verify(mapper).getInventoryByFacilityAndProgram(facilityId, programId);
    verify(equipmentMapper).getById(equipmentId);
    verify(equipmentTypeMapper).getEquipmentTypeById(equipmentTypeId);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    assertEquals(results, inventories);
  }

  @Test
  public void shouldGetCCEInventory() throws Exception {
    // Set up variables
    equipmentType.setColdChain(true);
    inventory.setEquipment(coldChainEquipment);
    List<EquipmentInventory> inventories = new ArrayList<>();
    inventories.add(inventory);
    Pagination page = new Pagination(1, 2);
    long[] facilityIds = {facilityId,facilityId2};
    String strFacilityIds = "{"+facilityId+","+facilityId2+"}";

    // Set up mock calls
    when(mapper.getInventory(programId, equipmentTypeId, strFacilityIds, page)).thenReturn(inventories);
    when(equipmentMapper.getById(equipmentId)).thenReturn(equipment);
    when(equipmentTypeMapper.getEquipmentTypeById(equipmentTypeId)).thenReturn(equipmentType);
    when(coldChainEquipmentMapper.getById(equipmentId)).thenReturn(coldChainEquipment);
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    List<EquipmentInventory> results = repository.getInventory(programId, equipmentTypeId, facilityIds, page);

    // Test the results
    verify(mapper).getInventory(programId, equipmentTypeId, strFacilityIds, page);
    verify(equipmentMapper).getById(equipmentId);
    verify(equipmentTypeMapper).getEquipmentTypeById(equipmentTypeId);
    verify(coldChainEquipmentMapper).getById(equipmentId);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    assertEquals(results, inventories);
  }

  @Test
  public void shouldGetNonCCEInventory() throws Exception {
    // Set up variables
    equipmentType.setColdChain(false);
    inventory.setEquipment(equipment);
    List<EquipmentInventory> inventories = new ArrayList<>();
    inventories.add(inventory);
    Pagination page = new Pagination(1, 2);
    long[] facilityIds = {facilityId,facilityId2};
    String strFacilityIds = "{"+facilityId+","+facilityId2+"}";

    // Set up mock calls
    when(mapper.getInventory(programId, equipmentTypeId, strFacilityIds, page)).thenReturn(inventories);
    when(equipmentMapper.getById(equipmentId)).thenReturn(equipment);
    when(equipmentTypeMapper.getEquipmentTypeById(equipmentTypeId)).thenReturn(equipmentType);
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    List<EquipmentInventory> results = repository.getInventory(programId, equipmentTypeId, facilityIds, page);

    // Test the results
    verify(mapper).getInventory(programId, equipmentTypeId, strFacilityIds, page);
    verify(equipmentMapper).getById(equipmentId);
    verify(equipmentTypeMapper).getEquipmentTypeById(equipmentTypeId);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    assertEquals(results, inventories);
  }

  @Test
  public void shouldGetInventoryCount() throws Exception {
    // Set up variables
    long[] facilityIds = {facilityId,facilityId2};
    String strFacilityIds = "{"+facilityId+","+facilityId2+"}";

    // Set up mock calls
    when(mapper.getInventoryCount(programId, equipmentTypeId, strFacilityIds)).thenReturn(2);

    // Do the call
    int count = repository.getInventoryCount(programId, equipmentTypeId, facilityIds);

    // Test the results
    verify(mapper).getInventoryCount(programId, equipmentTypeId, strFacilityIds);
    assertEquals(count, 2);
  }

  @Test
  public void shouldGetCCEInventoryById() throws Exception {
    // Set up variables
    equipmentType.setColdChain(true);
    inventory.setEquipment(coldChainEquipment);

    // Set up mock calls
    when(mapper.getInventoryById(inventoryId)).thenReturn(inventory);
    when(equipmentMapper.getById(equipmentId)).thenReturn(equipment);
    when(equipmentTypeMapper.getEquipmentTypeById(equipmentTypeId)).thenReturn(equipmentType);
    when(coldChainEquipmentMapper.getById(equipmentId)).thenReturn(coldChainEquipment);
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    EquipmentInventory result = repository.getInventoryById(inventoryId);

    // Test the results
    verify(mapper).getInventoryById(inventoryId);
    verify(equipmentMapper).getById(equipmentId);
    verify(equipmentTypeMapper).getEquipmentTypeById(equipmentTypeId);
    verify(coldChainEquipmentMapper).getById(equipmentId);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    assertEquals(result, inventory);
  }

  @Test
  public void shouldGetNonCCEInventoryById() throws Exception {
    // Set up variables
    equipmentType.setColdChain(false);
    inventory.setEquipment(equipment);

    // Set up mock calls
    when(mapper.getInventoryById(inventoryId)).thenReturn(inventory);
    when(equipmentMapper.getById(equipmentId)).thenReturn(equipment);
    when(equipmentTypeMapper.getEquipmentTypeById(equipmentTypeId)).thenReturn(equipmentType);
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    EquipmentInventory result = repository.getInventoryById(inventoryId);

    // Test the results
    verify(mapper).getInventoryById(inventoryId);
    verify(equipmentMapper).getById(equipmentId);
    verify(equipmentTypeMapper).getEquipmentTypeById(equipmentTypeId);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    assertEquals(result, inventory);
  }

  @Test
  public void shouldInsert() throws Exception {
    // Set up mock calls
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    repository.insert(inventory);

    // Test the results
    verify(mapper).insert(inventory);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
  }

  @Test
  public void shouldUpdate() throws Exception {
    // Set up mock calls
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    repository.update(inventory);

    // Test the results
    verify(mapper).update(inventory);
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
  }

  @Test
  public void shouldUpdateStatusWhenDifferent() throws Exception {
    // Set up variables
    long newStatusId = 3L;
    inventory.setOperationalStatusId(newStatusId);
    EquipmentInventoryStatus newStatus = new EquipmentInventoryStatus();
    newStatus.setInventoryId(inventoryId);
    newStatus.setStatusId(newStatusId);
    newStatus.setNotFunctionalStatusId(notFunctionalStatusId);

    // Set up mock calls
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    repository.updateStatus(inventory);

    // Test the results
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
    verify(equipmentInventoryStatusMapper).insert(newStatus);
  }

  @Test
  public void shouldNotUpdateStatusWhenSame() throws Exception {
    // Set up mock calls
    when(equipmentInventoryStatusMapper.getCurrentStatus(inventoryId)).thenReturn(status);

    // Do the call
    repository.updateStatus(inventory);

    // Test the results
    verify(equipmentInventoryStatusMapper).getCurrentStatus(inventoryId);
  }
}