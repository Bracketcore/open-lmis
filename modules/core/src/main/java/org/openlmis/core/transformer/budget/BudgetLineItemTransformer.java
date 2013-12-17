package org.openlmis.core.transformer.budget;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.transformer.LineItemTransformer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import static java.math.RoundingMode.FLOOR;

@Service
public class BudgetLineItemTransformer extends LineItemTransformer {


  private MessageService messageService = MessageService.getRequestInstance();

  public BudgetLineItem transform(BudgetLineItemDTO lineItemDTO, String datePattern, Integer rowNumber) {
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    Date periodDate = null;
    if (datePattern != null) {
      try {
        periodDate = parseDate(datePattern, lineItemDTO.getPeriodStartDate());
      } catch (Exception e) {
        throw new DataException(messageService.message("budget.invalid.date.format", lineItemDTO.getPeriodStartDate(), rowNumber));
      }
    }

    budgetLineItem.setFacilityCode(lineItemDTO.getFacilityCode());
    budgetLineItem.setProgramCode(lineItemDTO.getProgramCode());
    budgetLineItem.setPeriodDate(periodDate);

    String allocatedBudget = lineItemDTO.getAllocatedBudget();
    try {
      Double budget = Double.valueOf(allocatedBudget);
      if (budget < 0) {
        throw new DataException("Negative budget");
      }
      DecimalFormat decimalFormat = new DecimalFormat("#0.##");
      decimalFormat.setRoundingMode(FLOOR);
      allocatedBudget = decimalFormat.format(budget);
    } catch (Exception e) {
      throw new DataException(messageService.message("budget.allocated.budget.invalid", allocatedBudget, rowNumber));
    }

    budgetLineItem.setAllocatedBudget(new BigDecimal(allocatedBudget));
    budgetLineItem.setNotes(lineItemDTO.getNotes());

    return budgetLineItem;
  }
}
