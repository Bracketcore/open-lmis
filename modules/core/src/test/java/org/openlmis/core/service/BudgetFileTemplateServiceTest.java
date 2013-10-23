package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.repository.BudgetFileTemplateRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BudgetFileTemplateService.class)
@Category(UnitTests.class)
public class BudgetFileTemplateServiceTest {

  @Mock
  BudgetFileTemplateRepository budgetFileTemplateRepository;

  @InjectMocks
  BudgetFileTemplateService budgetFileTemplateService;

  @Test
  public void shouldUpdateBudgetFileTemplate() {

    EDIConfiguration ediConf = new EDIConfiguration();
    EDIFileColumn ediFileColumn1 = new EDIFileColumn("column1", "Column 1", true, true, 1, null);
    EDIFileColumn ediFileColumn2 = new EDIFileColumn("column2", "Column 2", true, true, 2, null);
    List<EDIFileColumn> columns = asList(ediFileColumn1, ediFileColumn2);
    EDIFileTemplate ediFileTemplate = new EDIFileTemplate(ediConf, columns);

    budgetFileTemplateService.update(ediFileTemplate);

    verify(budgetFileTemplateRepository).updateBudgetConfiguration(ediConf);
    verify(budgetFileTemplateRepository).update(ediFileColumn1);
    verify(budgetFileTemplateRepository).update(ediFileColumn2);
  }

  @Test
  public void shouldGetBudgetFileTemplate() throws Exception {

    List<EDIFileColumn> budgetFileColumns = new ArrayList<>();
    EDIConfiguration budgetConf = new EDIConfiguration();
    EDIFileTemplate ediFileTemplate = new EDIFileTemplate();
    when(budgetFileTemplateRepository.getAllBudgetFileColumns()).thenReturn(budgetFileColumns);
    when(budgetFileTemplateRepository.getBudgetConfiguration()).thenReturn(budgetConf);
    whenNew(EDIFileTemplate.class).withArguments(budgetConf, budgetFileColumns).thenReturn(ediFileTemplate);

    EDIFileTemplate returnedEdiFileTemplate = budgetFileTemplateService.get();

    assertThat(returnedEdiFileTemplate, is(ediFileTemplate));
  }

}
