/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class RefrigeratorReadingDTO extends BaseModel {

  Refrigerator refrigerator;
  //Readings
  Reading temperature;
  Reading functioningCorrectly;
  Reading lowAlarmEvents;
  Reading highAlarmEvents;
  Reading problemSinceLastTime;
  RefrigeratorProblem problems;
  String notes;

  public RefrigeratorReading transform() {
    refrigerator.validate();
    if ("Y".equalsIgnoreCase(problemSinceLastTime.getEffectiveValue())) {
      problems.validate();
    } else {
      problems = null;
    }

    return new RefrigeratorReading(refrigerator, null,
      temperature.parseFloat(),
      functioningCorrectly.getEffectiveValue(),
      lowAlarmEvents.parsePositiveInt(),
      highAlarmEvents.parsePositiveInt(),
      problemSinceLastTime.getEffectiveValue(),
      problems,
      notes);
  }
}
