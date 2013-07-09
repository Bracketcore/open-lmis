package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.domain.RegimenTemplate;
import org.openlmis.core.repository.RegimenColumnRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RegimenColumnService.class)
@Category(UnitTests.class)
public class RegimenColumnServiceTest {

  @Mock
  RegimenColumnRepository repository;

  @Mock
  MessageService messageService;

  @Mock
  ProgramService programService;

  @InjectMocks
  RegimenColumnService service;

  Long userId = 1L;

  @Test
  public void shouldCallSaveAndSetRegimenTemplateConfigured() throws Exception {

    Long programId = 1L;
    RegimenTemplate regimenTemplate = new RegimenTemplate(programId, new ArrayList<RegimenColumn>());

    service.save(regimenTemplate, userId);

    verify(repository).save(regimenTemplate, userId);
    verify(programService).setRegimenTemplateConfigured(programId);
  }

  @Test
  public void shouldGetRegimenColumnsByProgramId() throws Exception {
    Long programId = 1L;
    RegimenColumn regimenColumn1 = new RegimenColumn(programId, "testName1", "testLabel1", "numeric", true);
    RegimenColumn regimenColumn2 = new RegimenColumn(programId, "testName2", "testLabel2", "numeric", true);

    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(asList(regimenColumn1, regimenColumn2));

    List<RegimenColumn> resultColumns = service.getRegimenColumnsByProgramId(programId);

    verify(repository).getRegimenColumnsByProgramId(programId);
    assertThat(resultColumns.size(), is(2));
    assertThat(resultColumns.get(0), is(regimenColumn1));
    assertThat(resultColumns.get(1), is(regimenColumn2));
  }

  @Test
  public void shouldGetRegimenTemplateForProgram() throws Exception {
    ArrayList<RegimenColumn> regimenColumns = new ArrayList<RegimenColumn>() {{
      add(new RegimenColumn());
    }};
    RegimenTemplate regimenTemplate = new RegimenTemplate(1L, regimenColumns);
    when(repository.getRegimenColumnsByProgramId(1L)).thenReturn(regimenColumns);
    whenNew(RegimenTemplate.class).withArguments(1L, regimenColumns).thenReturn(regimenTemplate);

    RegimenTemplate template = service.getRegimenTemplate(1L);

    verifyNew(RegimenTemplate.class).withArguments(1L, regimenColumns);
    assertThat(template, is(regimenTemplate));
    verify(repository).getRegimenColumnsByProgramId(1L);
  }


  @Test
  public void shouldGetMasterRegimenTemplateForProgramIfRegimenTemplateNotConfigured() throws Exception {
    ArrayList<RegimenColumn> regimenColumns = new ArrayList<>();
    ArrayList<RegimenColumn> masterRegimenColumns = new ArrayList<RegimenColumn>() {{
      add(new RegimenColumn());
    }};

    RegimenTemplate regimenTemplate = new RegimenTemplate(1L, masterRegimenColumns);
    when(repository.getRegimenColumnsByProgramId(1L)).thenReturn(regimenColumns);
    when(repository.getMasterRegimenColumnsByProgramId()).thenReturn(masterRegimenColumns);
    whenNew(RegimenTemplate.class).withArguments(1L, masterRegimenColumns).thenReturn(regimenTemplate);

    RegimenTemplate template = service.getRegimenTemplate(1L);

    verifyNew(RegimenTemplate.class).withArguments(1L, masterRegimenColumns);
    assertThat(template, is(regimenTemplate));
    verify(repository).getRegimenColumnsByProgramId(1L);
    verify(repository).getMasterRegimenColumnsByProgramId();
  }
}
