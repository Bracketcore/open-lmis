package org.openlmis.core.transformer.budget;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.transformer.LineItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class BudgetLineItemTransformer extends LineItemTransformer {


  @Autowired
  private MessageService messageService;

  public BudgetLineItem transform(BudgetLineItemDTO lineItemDTO, String datePattern) {
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    Date periodDate = null;
    if (datePattern != null) {
      try {
        periodDate = parseDate(datePattern, lineItemDTO.getPeriodStartDate());
      } catch (Exception e) {
        throw new DataException(messageService.message("budget.invalid.date.format", lineItemDTO.getPeriodStartDate()));
      }
    }

    budgetLineItem.setFacilityCode(lineItemDTO.getFacilityCode());
    budgetLineItem.setProgramCode(lineItemDTO.getProgramCode());
    budgetLineItem.setPeriodDate(periodDate);
    budgetLineItem.setAllocatedBudget(BigDecimal.valueOf(Double.parseDouble(lineItemDTO.getAllocatedBudget())));
    budgetLineItem.setNotes(lineItemDTO.getNotes());

    return budgetLineItem;
  }
}
