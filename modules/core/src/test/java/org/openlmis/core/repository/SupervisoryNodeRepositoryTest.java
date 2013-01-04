package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private RequisitionGroupMemberRepository requisitionGroupMemberRepository;

  private SupervisoryNodeRepository repository;
  private SupervisoryNode supervisoryNodeWithParent;

  @Before
  public void setUp() throws Exception {
    supervisoryNodeWithParent = new SupervisoryNode();
    supervisoryNodeWithParent.setId(10);
    supervisoryNodeWithParent.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20);
    supervisoryNodeWithParent.setParent(parent);
    repository = new SupervisoryNodeRepository(supervisoryNodeMapper, facilityRepository, requisitionGroupMemberRepository);
  }

  @Test
  public void shouldGiveErrorIfDuplicateCodeFound() throws Exception {
    doThrow(new DuplicateKeyException("")).when(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
    when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNodeWithParent);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate SupervisoryNode Code");

    repository.save(supervisoryNodeWithParent);

    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldGiveErrorIfParentNodeCodeDoesNotExist() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenThrow(new DataException("Invalid SupervisoryNode Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervisory Node Parent does not exist");

    repository.save(supervisoryNodeWithParent);

    verify(supervisoryNodeMapper).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(1);
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeMapper).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(supervisoryNodeWithParent.getParent().getId());
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenReturn(1);

    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    assertThat(supervisoryNodeWithParent.getParent().getId(), is(20));
    assertThat(supervisoryNodeWithParent.getFacility().getId(), is(1));
    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldSaveSupervisoryNodeIfParentNotSupplied() throws Exception {
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenReturn(1);
    supervisoryNodeWithParent.setParent(null);
    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeMapper, never()).getIdForCode(anyString());
    assertThat(supervisoryNodeWithParent.getParent(), is(nullValue()));
    assertThat(supervisoryNodeWithParent.getFacility().getId(), is(1));
    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(repository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid SupervisoryNode Code");
    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldReturnParentIdForASupervisoryNode() {
    when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNodeWithParent);

    supervisoryNodeWithParent.getParent().setId(null);
    assertThat(repository.getSupervisoryNodeParentId(10), is(nullValue()));

    supervisoryNodeWithParent.getParent().setId(20);
    assertThat(repository.getSupervisoryNodeParentId(10), is(20));
  }

  @Test
  public void shouldGetSupervisoryNodeForFacilityProgram() throws Exception {
    int facilityId = 1;
    int programId = 1;
    SupervisoryNode expectedSupervisoryNode = new SupervisoryNode();
    String rgCode = "test code";
    when(requisitionGroupMemberRepository.getRGCodeForProgramAndFacility(facilityId, programId)).thenReturn(rgCode);
    when(supervisoryNodeMapper.getFor(rgCode)).thenReturn(expectedSupervisoryNode);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facilityId, programId);

    assertThat(actualSupervisoryNode, is(expectedSupervisoryNode));
  }
}
