/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramSupported extends BaseModel implements Importable {

  private Long facilityId;

  @ImportField(mandatory = true, name = "Program Code", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Facility Code")
  private String facilityCode;

  @ImportField(mandatory = true, name = "Program Is Active", type = "boolean")
  private Boolean active = false;

  @ImportField(name = "Program Start Date", type = "Date")
  private Date startDate;

  private List<FacilityProgramProduct> programProducts;

  public void isValid() {
    if (this.active && this.startDate == null)
      throw new DataException("supported.programs.invalid");
  }

  public ProgramSupported(Long programId, Boolean active, Date startDate) {

    this.program = new Program(programId);
    this.active = active;
    this.startDate = startDate;
  }

  @SuppressWarnings("unused")
  public String getStringStartDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return simpleDateFormat.format(this.startDate);
  }
}
