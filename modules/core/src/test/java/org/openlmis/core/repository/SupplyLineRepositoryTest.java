/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SupplyLineRepositoryTest {
  @Mock
  private SupplyLineMapper mapper;
  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private FacilityRepository facilityRepository;

  private SupplyLineRepository repository;
  private SupplyLine supplyLine;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() {
    repository = new SupplyLineRepository(mapper);
    supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
  }

  @Test
  public void shouldInsertSupplyLine() {
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);

    repository.insert(supplyLine);
    verify(mapper).insert(supplyLine);
  }

  @Test
  public void shouldThrowExceptionForDuplicateSupplyLines() {
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);
    doThrow(new DataException("Duplicate entry for Supply Line found")).when(mapper).insert(supplyLine);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate entry for Supply Line found");

    repository.insert(supplyLine);
  }

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeAndProgram() {
    Program program = new Program();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    when(mapper.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = repository.getSupplyLineBy(supervisoryNode, program);

    verify(mapper).getSupplyLineBy(supervisoryNode, program);
    assertThat(returnedSupplyLine, is(supplyLine));
  }

  @Test
  public void shouldGetSupplyLineById() {
    when(mapper.getById(3L)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = repository.getById(3L);

    verify(mapper).getById(3L);
    assertThat(returnedSupplyLine, is(supplyLine));
  }

  @Test
  public void shouldSearchByFacilityName() throws Exception {
    String searchParam = "fac";
    String columnName = "facility";
    List<SupplyLine> supplyLines = asList(new SupplyLine());

    Pagination pagination = new Pagination(2, 10);
    when(mapper.searchByFacilityName(searchParam, pagination)).thenReturn(supplyLines);

    List<SupplyLine> result = repository.search(searchParam, columnName, pagination);
    assertThat(result, is(supplyLines));
    verify(mapper).searchByFacilityName(searchParam, pagination);
  }

  @Test
  public void shouldGetCountOfSearchResultsByFacilityName() throws Exception {
    String searchParam = "fac";
    String columnName = "facility";

    when(mapper.getTotalSearchResultsByFacilityName(searchParam)).thenReturn(1);

    Integer result = repository.getTotalSearchResultCount(searchParam, columnName);
    assertThat(result, is(1));
    verify(mapper).getTotalSearchResultsByFacilityName(searchParam);
  }

  @Test
  public void shouldSearchBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";
    String columnName = "supervisoryNode";
    List<SupplyLine> supplyLines = asList(new SupplyLine());

    Pagination pagination = new Pagination(2, 10);
    when(mapper.searchBySupervisoryNodeName(searchParam, pagination)).thenReturn(supplyLines);

    List<SupplyLine> result = repository.search(searchParam, columnName, pagination);
    assertThat(result, is(supplyLines));
    verify(mapper).searchBySupervisoryNodeName(searchParam, pagination);
  }

  @Test
  public void shouldGetCountOfSearchResultsBySupervisoryNodeName() throws Exception {
    String searchParam = "nod";
    String columnName = "supervisoryNode";

    when(mapper.getTotalSearchResultsBySupervisoryNodeName(searchParam)).thenReturn(1);

    Integer result = repository.getTotalSearchResultCount(searchParam, columnName);
    assertThat(result, is(1));
    verify(mapper).getTotalSearchResultsBySupervisoryNodeName(searchParam);
  }
}
