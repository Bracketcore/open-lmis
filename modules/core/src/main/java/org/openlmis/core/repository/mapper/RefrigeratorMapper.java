/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Refrigerator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefrigeratorMapper {

  @Insert({"INSERT INTO refrigerators",
    "(brand, model, serialNumber, facilityId, createdBy, modifiedBy)",
    "VALUES",
    "(#{brand}, #{model}, #{serialNumber}, #{facilityId} ,#{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Refrigerator refrigerator);

  @Select({"SELECT RF.*",
    "FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
    "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
    "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
    "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
    "INNER JOIN refrigerators RF ON RF.facilityId = F.id",
    "WHERE DZPS.programId = #{programId} AND F.active = true",
    "AND PS.programId = #{programId}  AND DZM.deliveryZoneId = #{deliveryZoneId} order by F.name"})
  List<Refrigerator> getRefrigeratorsForADeliveryZoneAndProgram(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);

}
