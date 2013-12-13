package org.openlmis.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Service
public class BudgetFileProcessor {

  private static Logger logger = Logger.getLogger(BudgetFileProcessor.class);

  @Autowired
  private BudgetFileTemplateService budgetFileTemplateService;

  @Autowired
  private BudgetLineItemTransformer budgetLineItemTransformer;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private BudgetFileService budgetFileService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  private BudgetLineItemService budgetLineItemService;

  @Autowired
  private BudgetFilePostProcessHandler budgetFilePostProcessHandler;

  @Autowired
  private MessageService messageService;


  public void process(Message message) throws IOException {

    File budgetFile = (File) message.getPayload();

    logger.debug("processing Budget File " + budgetFile.getName());

    Boolean processingError = false;

    BudgetFileInfo budgetFileInfo = saveBudgetFile(budgetFile, processingError);

    EDIFileTemplate budgetFileTemplate = budgetFileTemplateService.get();

    ICsvListReader listReader = new CsvListReader(new FileReader(budgetFile), STANDARD_PREFERENCE);

    if (budgetFileTemplate.getConfiguration().isHeaderInFile()) listReader.getHeader(true);

    List<String> csvRow;
    Integer rowNumber;
    while ((csvRow = listReader.read()) != null) {
      Collection<EDIFileColumn> includedColumns = budgetFileTemplate.filterIncludedColumns();

      BudgetLineItemDTO budgetLineItemDTO = BudgetLineItemDTO.populate(csvRow, includedColumns);
      try {
        budgetLineItemDTO.checkMandatoryFields();
        rowNumber = listReader.getRowNumber();
        Facility facility = validateFacility(budgetLineItemDTO.getFacilityCode(), rowNumber);
        Program program = validateProgram(budgetLineItemDTO.getProgramCode(), rowNumber);

        BudgetLineItem budgetLineItem = getBudgetLineItem(includedColumns, budgetLineItemDTO, rowNumber);

        ProcessingPeriod processingPeriod = validatePeriod(facility, program, budgetLineItem.getPeriodDate(), rowNumber);

        budgetLineItem.setPeriodId(processingPeriod.getId());
        budgetLineItem.setBudgetFileId(budgetFileInfo.getId());
        budgetLineItemService.save(budgetLineItem);
      } catch (Exception e) {
        processingError = true;
        logger.error(e.getMessage(), e);
        continue;
      }
    }
    Integer blankFileRows = budgetFileTemplate.getConfiguration().isHeaderInFile() ? 1 : 0;
    if (listReader.getRowNumber() == blankFileRows) {
      logger.error(messageService.message("error.facility.code.invalid"));
      processingError = true;
    }
    budgetFileInfo.setProcessingError(processingError);
    budgetFilePostProcessHandler.process(budgetFileInfo, budgetFile);
  }

  private BudgetLineItem getBudgetLineItem(Collection<EDIFileColumn> includedColumns, BudgetLineItemDTO budgetLineItemDTO, Integer rowNumber) {
    EDIFileColumn periodDateColumn = (EDIFileColumn) CollectionUtils.find(includedColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        EDIFileColumn periodDateColumn = (EDIFileColumn) o;
        return periodDateColumn.getName().equals("periodStartDate") && periodDateColumn.getInclude();
      }
    });
    String datePattern = periodDateColumn == null ? null : periodDateColumn.getDatePattern();
    return budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  private BudgetFileInfo saveBudgetFile(File budgetFile, Boolean processingError) {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo(budgetFile.getName(), processingError);

    budgetFileService.save(budgetFileInfo);

    return budgetFileInfo;
  }

  private ProcessingPeriod validatePeriod(Facility facility, Program program, Date date, Integer rowNumber) {
    ProcessingPeriod periodForDate = processingScheduleService.getPeriodForDate(facility, program, date);
    if (periodForDate == null) {
      throw new DataException(messageService.message("budget.start.date.invalid", date, facility.getCode(), program.getCode(), rowNumber));
    }
    return periodForDate;
  }

  private Program validateProgram(String programCode, Integer rowNumber) {
    Program program = programService.getByCode(programCode);
    if (program == null) {
      throw new DataException(messageService.message("budget.program.code.invalid", programCode, rowNumber));
    }
    return program;
  }

  private Facility validateFacility(String facilityCode, Integer rowNumber) {
    Facility facility = new Facility();
    facility.setCode(facilityCode);
    if ((facility = facilityService.getByCode(facility)) == null) {
      throw new DataException(messageService.message("budget.facility.code.invalid", facilityCode, rowNumber));
    }
    return facility;
  }
}