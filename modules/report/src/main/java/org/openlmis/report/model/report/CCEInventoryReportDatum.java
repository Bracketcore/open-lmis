
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.model.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCEInventoryReportDatum implements ReportData {

    private Integer equipmentId;
    private String manufacturer;
    private String model;
    private String energyTypeName;
    private String equipmentColdChainEquipmentsCode;

    private String refrigerant;
    private Integer refrigeratorCapacity;
    private Integer freezerCapacity;
    private String equipmentOperationalStatusName;
    private Integer yearOfInstallation;
    private Double equipmentAge; //TODO: Use an Integer instead
    private Integer yearOfReplacement;

    private Integer facilityId;
    private String facilityName;
    private String facilityTypeName;
    private boolean facilityHasElectricity;
    private String facilityAddress1;
    private String facilityAddress2;

    private  String facilityOperator;

    private Integer geozoneId;
    private String geozoneName;
    private String geozoneHierarchy;


    //The private geozoneHierarchy member is given a value of the form {zone1, zone2, zone4}
    //This method strips off the leading and trailing curly-braces and return the tokenized result.
    public List<String> getGeozoneHierarchy()
    {
        if(geozoneHierarchy == null || geozoneHierarchy.equals(""))
            return new ArrayList<String>();

        String zones = geozoneHierarchy.substring(1, geozoneHierarchy.length()-1);
        return Arrays.asList(zones.split(","));
    }

    //Temporary convenience method used for designing Jasper reports
    private static List<CCEInventoryReportDatum> getBeanCollection()
    {
        ArrayList<CCEInventoryReportDatum> list = new ArrayList<CCEInventoryReportDatum>();
        list.add(getTestDatum(1));
        return list;
    }

    private static CCEInventoryReportDatum getTestDatum(Integer seed)
    {
        CCEInventoryReportDatum datum = new CCEInventoryReportDatum();
        datum.equipmentId = seed;
        datum.manufacturer = "manufacturer_" + seed;
        datum.model = "model_" + seed;
        datum.energyTypeName = "energyTypeName_" + seed;
        datum.equipmentColdChainEquipmentsCode = "code_" + seed;

        datum.refrigerant = "refrigerant_" + seed;
        datum.refrigeratorCapacity = seed;
        datum.freezerCapacity = seed;
        datum.equipmentOperationalStatusName = "equipmentOperationalStatusName_" + seed;
        datum.yearOfInstallation = 2015;
        datum.equipmentAge = 0.0;
        datum.yearOfReplacement = 0;

        datum.facilityId = seed;
        datum.facilityName = "facilityName_" + seed;
        datum.facilityTypeName = "facilityTypeName_" + seed;
        datum.facilityHasElectricity = true;
        datum.facilityAddress1 = "facilityAddress1_" + seed;
        datum.facilityAddress2 = "facilityAddress2_" + seed;

        datum.facilityOperator = "facilityOperator_" + seed;

        datum.geozoneId = seed;
        datum.geozoneName = "geozoneName_" + seed;
        datum.geozoneHierarchy = "{Karatu,Arusha,\"Moshi Zone\",Tanzania}";

        return datum;
    }

}
