/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProcessingPeriod extends BaseModel {

  private Long scheduleId;

  private String name;
  private String description;
  private Date startDate;
  private Date endDate;
  private Integer numberOfMonths;

  public ProcessingPeriod(Long id) {
    this.id = id;
  }

  public ProcessingPeriod(Long id, Date startDate, Date endDate, Integer numberOfMonths, String name) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfMonths = numberOfMonths;
    this.name = name;
  }

  public void validate() {
    if (scheduleId == null || scheduleId == 0)
      throw new DataException("Period can not be saved without its parent Schedule.");
    if (startDate == null || startDate.toString().isEmpty())
      throw new DataException("Period can not be saved without its Start Date.");
    if (endDate == null || endDate.toString().isEmpty())
      throw new DataException("Period can not be saved without its End Date.");
    if (name == null || name.isEmpty())
      throw new DataException("Period can not be saved without its Name.");
    if (endDate.compareTo(startDate)<=0) {
      throw new DataException("Period End Date can not be earlier than Start Date.");
    }
  }

  public ProcessingPeriod basicInformation() {
    return new ProcessingPeriod(id, startDate, endDate, numberOfMonths, name);
  }
}
