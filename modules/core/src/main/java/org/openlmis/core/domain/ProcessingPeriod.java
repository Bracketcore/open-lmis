package org.openlmis.core.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ProcessingPeriod {

  private Integer id;
  private Integer scheduleId;

  private String name;
  private String description;
  private Integer modifiedBy;
  private Date startDate;
  private Date endDate;
  private Date modifiedDate;

  public void validate() {
    if (scheduleId == null || scheduleId == 0)
      throw new RuntimeException("Period can not be saved without its parent Schedule.");
    if (startDate == null || startDate.toString().isEmpty())
      throw new RuntimeException("Period can not be saved without its Start Date.");
    if (endDate == null || endDate.toString().isEmpty())
      throw new RuntimeException("Period can not be saved without its End Date.");
    if (name == null || name.isEmpty())
      throw new RuntimeException("Period can not be saved without its Name.");
    if (endDate.before(startDate)) {
      throw new RuntimeException("Period End Date can not be earlier than Start Date.");
    }
  }

}
