/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Money;
import org.openlmis.core.service.MessageService;
import org.openlmis.rnr.domain.*;
import org.openlmis.web.model.PrintRnrLineItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.*;

@Data
@NoArgsConstructor
public class RequisitionPdfModel {
  public static final String LABEL_CURRENCY_SYMBOL = "label.currency.symbol";
  private List<RequisitionStatusChange> statusChanges;
  private Map<String, Object> model;
  public static final float PARAGRAPH_SPACING = 30.0f;
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int TABLE_SPACING = 25;

  private List<? extends Column> rnrColumnList;
  private List<? extends Column> regimenColumnList;
  private Rnr requisition;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;
  private MessageService messageService;

  public RequisitionPdfModel(Map<String, Object> model, MessageService messageService) {
    this.model = model;
    this.statusChanges = (List<RequisitionStatusChange>) model.get(STATUS_CHANGES);
    this.rnrColumnList = (List<RnrColumn>) model.get(RNR_TEMPLATE);
    this.regimenColumnList = (List<RegimenColumn>) model.get(REGIMEN_TEMPLATE);
    this.requisition = (Rnr) model.get(RNR);
    this.lossesAndAdjustmentsTypes = (List<LossesAndAdjustmentsType>) model.get(LOSSES_AND_ADJUSTMENT_TYPES);
    this.messageService = messageService;
  }

  public Paragraph getFullSupplyHeader() {
    return new Paragraph(messageService.message("label.full.supply.products"), H2_FONT);
  }

  public Paragraph getNonFullSupplyHeader() {
    return new Paragraph(messageService.message("label.non.full.supply.products"), H2_FONT);
  }

  public PdfPTable getFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    return getTableFor(requisition.getFullSupplyLineItems(), true, rnrColumnList);
  }

  public PdfPTable getRegimenTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    List<RegimenLineItem> regimenLineItems = requisition.getRegimenLineItems();
    if (regimenLineItems.size() == 0) return null;

    return getTableFor(regimenLineItems, null, regimenColumnList);
  }

  private PdfPTable getTableFor(List<? extends LineItem> lineItems, Boolean fullSupply, List<? extends Column> columnList) throws DocumentException, NoSuchFieldException, IllegalAccessException {
    Template template = Template.getInstance(columnList);
    List<? extends Column> visibleColumns = template.getPrintableColumns(fullSupply);

    PdfPTable table = prepareTable(visibleColumns);

    boolean odd = true;

    LineItem previousLineItem = null;
    for (LineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.compareCategory(previousLineItem)) {
        table.addCell(categoryRow(visibleColumns.size(), lineItem));
        previousLineItem = lineItem;
      }

      if (lineItem.isRnrLineItem()) {
        PrintRnrLineItem printRnrLineItem = new PrintRnrLineItem(lineItem);
        printRnrLineItem.calculate(requisition.getPeriod(), rnrColumnList, lossesAndAdjustmentsTypes);
      }

      List<PdfPCell> cells = getCells(visibleColumns, lineItem, messageService.message(LABEL_CURRENCY_SYMBOL));
      odd = !odd;

      for (PdfPCell cell : cells) {
        cell.setBackgroundColor(odd ? BaseColor.WHITE : ROW_GREY_BACKGROUND);
        table.addCell(cell);
      }
    }
    return table;
  }

  private void setTableHeader(PdfPTable table, List<? extends Column> visibleColumns) {
    for (Column column : visibleColumns) {
      table.addCell(column.getLabel());
    }
  }

  private PdfPTable prepareTable(List<? extends Column> visibleColumns) throws DocumentException {
    java.util.List<Integer> widths = new ArrayList<>();
    for (Column column : visibleColumns) {
      widths.add(column.getColumnWidth());
    }
    PdfPTable table = new PdfPTable(widths.size());

    table.setWidths(ArrayUtils.toPrimitive(widths.toArray(new Integer[widths.size()])));
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(CELL_PADDING);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(2);
    table.setFooterRows(1);
    setTableHeader(table, visibleColumns);
    setBlankFooter(table, visibleColumns.size());
    return table;
  }

  private void setBlankFooter(PdfPTable table, Integer visibleColumnsSize) {
    PdfPCell cell = new PdfPCell(new Phrase(" "));
    cell.setBorder(0);
    cell.setColspan(visibleColumnsSize);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell);
  }


  public PdfPTable getNonFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    List<RnrLineItem> nonFullSupplyLineItems = requisition.getNonFullSupplyLineItems();
    if (nonFullSupplyLineItems.size() == 0) return null;

    return getTableFor(nonFullSupplyLineItems, false, rnrColumnList);
  }

  public PdfPTable getRequisitionHeader() throws DocumentException {
    PdfPTable table = prepareRequisitionHeaderTable();
    addHeading(table);

    Facility facility = requisition.getFacility();
    addFirstLine(facility, table);
    addSecondLine(facility, table, requisition.getEmergency());
    table.setSpacingAfter(PARAGRAPH_SPACING);
    return table;
  }


  private void addHeading(PdfPTable table) throws DocumentException {
    Chunk chunk = new Chunk(String.format(messageService.message("label.requisition") + ": %s (%s)",
      this.requisition.getProgram().getName(),
      this.requisition.getFacility().getFacilityType().getName()), H1_FONT);

    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(4);
    cell.setPadding(10);
    cell.setBorder(0);
    table.addCell(cell);
  }

  private void addFirstLine(Facility facility, PdfPTable table) {
    String text = String.format(messageService.message("label.facility") + ": %s", facility.getName());
    insertCell(table, text, 1);
    text = String.format(messageService.message("create.facility.operatedBy") + ": %s", facility.getOperatedBy().getText());
    insertCell(table, text, 1);
    text = String.format(messageService.message("label.facility.maximumStock") + ": %s", facility.getFacilityType().getNominalMaxMonth());
    insertCell(table, text, 1);
    text = String.format(messageService.message("label.facility.emergencyOrder") + ": %s", facility.getFacilityType().getNominalEop());
    insertCell(table, text, 1);
  }

  private void insertCell(PdfPTable table, String text, int colspan) {
    Chunk chunk;
    chunk = new Chunk(text);
    PdfPCell cell = table.getDefaultCell();
    cell.setPhrase(new Phrase(chunk));
    cell.setColspan(colspan);
    table.addCell(cell);
  }

  private void addSecondLine(Facility facility, PdfPTable table, Boolean emergency) {
    GeographicZone geographicZone = facility.getGeographicZone();
    GeographicZone parent = geographicZone.getParent();
    StringBuilder builder = new StringBuilder();
    builder.append(geographicZone.getLevel().getName()).append(": ").append(geographicZone.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(parent.getLevel().getName()).append(": ").append(parent.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(messageService.message("label.facility.reportingPeriod") + ": ").append(DATE_FORMAT.format(requisition.getPeriod().getStartDate())).append(" - ").
      append(DATE_FORMAT.format(requisition.getPeriod().getEndDate()));
    insertCell(table, builder.toString(), 1);

    String label = emergency ? "requisition.type.emergency" : "requisition.type.regular";
    builder = new StringBuilder();
    builder.append(messageService.message("label.requisition.type")).append(": ").append(messageService.message(label));
    insertCell(table, builder.toString(), 1);

  }

  private PdfPTable prepareRequisitionHeaderTable() throws DocumentException {
    int[] columnWidths = {200, 200, 200, 200};
    PdfPTable table = new PdfPTable(columnWidths.length);
    table.setWidths(columnWidths);
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(10);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(1);
    return table;
  }

  public PdfPTable getSummary() throws DocumentException {
    this.requisition.fillFullSupplyCost();
    this.requisition.fillNonFullSupplyCost();

    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidths(new int[]{30, 20});
    summaryTable.setSpacingBefore(TABLE_SPACING);
    summaryTable.setWidthPercentage(40);
    summaryTable.setHorizontalAlignment(0);

    PdfPCell summaryHeaderCell = headingCell(messageService.message("label.summary"));
    summaryHeaderCell.setColspan(2);
    summaryHeaderCell.setPadding(10);
    summaryHeaderCell.setBorder(0);
    summaryTable.addCell(summaryHeaderCell);

    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost.full.supply.items"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + requisition.getFullSupplyItemsSubmittedCost())));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost.non.full.supply.items"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + requisition.getNonFullSupplyItemsSubmittedCost())));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + this.getTotalCost(requisition).toString())));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));

    fillAuditFields(summaryTable);
    return summaryTable;
  }

  private void fillAuditFields(PdfPTable summaryTable) {
    RequisitionStatusChange submittedStatusChange = getStatusChangeFor(SUBMITTED);
    RequisitionStatusChange authorizedStatusChange = getStatusChangeFor(AUTHORIZED);

    String submittedDate = submittedStatusChange != null ? DATE_FORMAT.format(submittedStatusChange.getCreatedDate()) : "";
    String submittedBy = submittedStatusChange != null ?
      submittedStatusChange.getCreatedBy().getFirstName() + " " + submittedStatusChange.getCreatedBy().getLastName() : "";

    String authorizedDate = authorizedStatusChange != null ? DATE_FORMAT.format(authorizedStatusChange.getCreatedDate()) : "";
    String authorizedBy = authorizedStatusChange != null ?
      authorizedStatusChange.getCreatedBy().getFirstName() + " " + authorizedStatusChange.getCreatedBy().getLastName() : "";

    summaryTable.addCell(summaryCell(textCell(messageService.message("label.submitted.by") + ": " + submittedBy)));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.date") + ": " + submittedDate)));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.authorized.by") + ": " + authorizedBy)));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.date") + ": " + authorizedDate)));
  }

  private RequisitionStatusChange getStatusChangeFor(final RnrStatus status) {
    return (RequisitionStatusChange) find(statusChanges, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((RequisitionStatusChange) o).getStatus().equals(status);
      }
    });
  }

  private PdfPCell summaryCell(PdfPCell cell) {
    cell.setPadding(15);
    cell.setBorder(0);
    return cell;
  }

  public Money getTotalCost(Rnr requisition) {
    return new Money(requisition.getFullSupplyItemsSubmittedCost().getValue().add(requisition.getNonFullSupplyItemsSubmittedCost().getValue()));
  }


  public Paragraph getRegimenHeader() {
    return new Paragraph(messageService.message("label.regimens"), H2_FONT);
  }
}
