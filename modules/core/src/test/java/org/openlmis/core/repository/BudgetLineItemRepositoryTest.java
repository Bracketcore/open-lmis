package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.mapper.BudgetLineItemMapper;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BudgetLineItemRepositoryTest {

  @Mock
  BudgetLineItemMapper mapper;

  @InjectMocks
  BudgetLineItemRepository repository;

  @Test
  public void shouldInsertBudgetLineItemIfDoesNotExistsAlready() throws Exception {

    BudgetLineItem budgetLineItem = new BudgetLineItem(1L, 2L, 1L, 1L, new Date(), BigDecimal.valueOf(345.45), "My good notes");

    repository.save(budgetLineItem);

    verify(mapper).insert(budgetLineItem);
  }

  @Test
  public void shouldUpdateBudgetLineItemIfAlreadyExists() throws Exception {
    Long periodId = 1l;
    BudgetLineItem budgetLineItem = new BudgetLineItem(1L, 2L, periodId, 1L, new Date(), BigDecimal.valueOf(345.45), "My good notes");

    doThrow(DuplicateKeyException.class).when(mapper).insert(budgetLineItem);

    repository.save(budgetLineItem);

    verify(mapper, never()).get(1L, 2L, periodId);
    verify(mapper).insert(budgetLineItem);
    verify(mapper).update(budgetLineItem);
  }
}
