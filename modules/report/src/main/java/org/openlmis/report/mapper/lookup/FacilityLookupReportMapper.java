/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Facility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityLookupReportMapper {

    @Select("SELECT id, code, name, geographicZoneId, typeId" +
            "   FROM " +
            "       facilities order by name")
    List<Facility> getAll();

    @Select("SELECT * " +
            "   FROM " +
            "       facilities where code = #{code}")
    Facility getFacilityByCode(String code);

    @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f join programs_supported ps " +
      "          on ps.facilityid = f.id " +
      "          where ps.programid = #{program} and ps.active = true  order by f.name")
    List<Facility> getFacilitiesByProgram(@Param("program") Long program);

  @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f  " +
      "          join programs_supported ps \n" +
      "            on ps.facilityid = f.id\n" +
      "          join requisition_group_members m \n" +
      "            on m.facilityId = f.id\n" +
      "          join requisition_group_program_schedules rps\n" +
      "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId " +
      "        where " +
      "             ps.programid = #{program} " +
      "             and rps.scheduleid = #{schedule} " +
      "             and ps.active = true  " +
      "        order by f.name")
    List<Facility> getFacilitiesByProgramSchedule(@Param("program") Long program, @Param("schedule") Long schedule);

  @Select("SELECT f.id, f.code, f.name" +
      "   FROM " +
      "       facilities f " +
      "          join programs_supported ps \n" +
      "            on ps.facilityid = f.id\n" +
      "          join requisition_group_members m \n" +
      "            on m.facilityId = f.id\n" +
      "          join requisition_group_program_schedules rps\n" +
      "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
      "        where " +
      "             ps.programid = #{program} " +
      "             and rps.scheduleid = #{schedule} " +
      "             and f.typeid = #{type} " +
      "             and ps.active = true  " +
      "        order by f.name")
    List<Facility> getFacilitiesByPrgraomScheduleType(@Param("program") Long program, @Param("schedule") Long schedule, @Param("type") Long type);

    @Select("SELECT DISTINCT f.id, f.code, f.name\n" +
            "         FROM  \n" +
            "             facilities f\n" +
            "                join geographic_zones gz on gz.id = f.geographicZoneId  \n" +
            "                join programs_supported ps \n" +
            "                on ps.facilityid = f.id\n" +
            "                join requisition_group_members m \n" +
            "                on m.facilityId = f.id\n" +
            "                join requisition_group_program_schedules rps\n" +
            "                on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
            "              where gz.id = #{geographicZoneId} \n"+
            "              and ps.active = true\n"+
            "              order by f.name")
    List<Facility> getFacilitiesByGeographicZone(@Param("geographicZoneId") Long geographicZoneId);

    @Select("SELECT DISTINCT f.id, f.code, f.name\n" +
            "         FROM  \n" +
            "             facilities f\n" +
            "                join geographic_zones gz on gz.id = f.geographicZoneId  \n" +
            "                join programs_supported ps \n" +
            "                on ps.facilityid = f.id\n" +
            "                join requisition_group_members m \n" +
            "                on m.facilityId = f.id\n" +
            "                join requisition_group_program_schedules rps\n" +
            "                on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
            "              where gz.id = #{geographicZoneId} \n"+
            "              and m.requisitionGroupId = #{requisitionGroupId}\n" +
            "              and ps.programid = #{programId}\n"+
            "              and ps.active = true\n"+
            "              order by f.name")
    List<Facility> getFacilitiesBy(@Param("geographicZoneId") Long geographicZoneId,@Param("requisitionGroupId") Long requisitionGroupId,@Param("programId") Long programId);

    @Select("SELECT DISTINCT f.id, f.code, f.name\n" +
            "         FROM  \n" +
            "             facilities f\n" +
            "                join geographic_zones gz on gz.id = f.geographicZoneId  \n" +
            "                join programs_supported ps \n" +
            "                on ps.facilityid = f.id\n" +
            "                join requisition_group_members m \n" +
            "                on m.facilityId = f.id\n" +
            "                join requisition_group_program_schedules rps\n" +
            "                on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
            "              where gz.id =  #{geographicZoneId} \n"+
            "              and m.requisitionGroupId = CASE WHEN #{requisitionGroupId} = 0 THEN m.requisitionGroupId ELSE #{requisitionGroupId} END\n" +
            "              and ps.programid = CASE WHEN #{programId} = 0 THEN ps.programid ELSE #{programId} END  \n"+
            "              and rps.scheduleid = CASE WHEN #{scheduleId} = 0 THEN rps.scheduleid ELSE #{scheduleId} END \n"+
            "              and ps.active = true\n"+
            "              order by f.name")
    List<Facility> getFacilitiesBy(@Param("geographicZoneId") Long geographicZoneId,@Param("requisitionGroupId") Long requisitionGroupId, @Param("programId") Long programId, @Param("scheduleId") Long scheduleId);
}
