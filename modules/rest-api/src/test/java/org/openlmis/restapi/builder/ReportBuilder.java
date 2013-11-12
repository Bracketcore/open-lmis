/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.restapi.domain.Report;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ReportBuilder {

  public static final Property<Report, Long> facilityId = newProperty();
  public static final Property<Report, Long> programId = newProperty();
  public static final Property<Report, Long> periodId = newProperty();
  public static final Property<Report, String> userId = newProperty();
  public static final Property<Report, Long> requisitionId = newProperty();

  public static final Instantiator<Report> defaultReport = new Instantiator<Report>() {
    @Override
    public Report instantiate(PropertyLookup<Report> lookup) {
      Report report = new Report();
      report.setRequisitionId(lookup.valueOf(requisitionId, 1L));
      report.setFacilityId(lookup.valueOf(facilityId, 1L));
      report.setProgramId(lookup.valueOf(programId, 1L));
      report.setPeriodId(lookup.valueOf(periodId, 1L));
      report.setUserName(lookup.valueOf(userId, "1"));
      report.setEmergency(false);
      return report;
    }
  };
}
