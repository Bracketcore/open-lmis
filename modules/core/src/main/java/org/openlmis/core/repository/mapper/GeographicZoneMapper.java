/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * GeographicZoneMapper maps the GeographicZone entity to corresponding representation in database.
 */
@Repository
public interface GeographicZoneMapper {

  @Insert("INSERT INTO geographic_zones (code, name, levelId, parentId, catchmentPopulation, longitude, latitude, createdBy, modifiedBy, modifiedDate) " +
    "VALUES (#{code}, #{name}, #{level.id}, #{parent.id}, #{catchmentPopulation}, #{longitude}, #{latitude}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(GeographicZone geographicZone);

  @Select("SELECT * FROM geographic_levels WHERE LOWER(code) = LOWER(#{code})")
  GeographicLevel getGeographicLevelByCode(String code);

  @Select({"SELECT GZ.id, GZ.code, GZ.name, GL.id AS levelId, GL.code AS levelCode, GL.name AS levelName, GL.levelNumber AS levelNumber ",
    "FROM geographic_zones GZ, geographic_levels GL ",
    "WHERE GZ.levelId = GL.id AND LOWER(GZ.code) <> 'root' AND ",
    "GL.levelNumber = (SELECT MAX(levelNumber) FROM geographic_levels) ORDER BY GZ.name"})
  @Results({
    @Result(column = "levelId", property = "level.id"),
    @Result(column = "levelCode", property = "level.code"),
    @Result(column = "levelName", property = "level.name"),
    @Result(column = "levelNumber", property = "level.levelNumber")
  })
  List<GeographicZone> getAllGeographicZones();

  @Select({"SELECT GZ.id, GZ.code, GZ.name, GZ.catchmentPopulation, GZ.longitude, GZ.latitude, GZ.modifiedDate, GL.id as levelId, GL.code as levelCode, GL.name as levelName,",
    "GL.levelNumber as levelNumber FROM",
    "geographic_zones GZ, geographic_levels GL WHERE LOWER(GZ.code) = LOWER(#{code}) AND GZ.levelId = GL.id"})
  @Results({
    @Result(column = "levelId", property = "level.id"),
    @Result(column = "levelCode", property = "level.code"),
    @Result(column = "levelName", property = "level.name"),
    @Result(column = "levelNumber", property = "level.levelNumber")
  })
  GeographicZone getGeographicZoneByCode(String code);


  @Select({"SELECT GZ.id AS id, GZ.code AS code, GZ.name AS name, GZ.catchmentPopulation, GZ.longitude, GZ.latitude,",
    " GL.id as levelId,GL. levelNumber as levelNumber, GL.code AS levelCode, GL.name AS level, GZP.code AS parentCode, GZP.name AS parentZone,",
    " GLP.code AS parentLevelCode, GLP.name AS parentLevel",
    "FROM geographic_zones GZ INNER JOIN geographic_zones GZP ON GZ.parentId = GZP.id",
    "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id",
    "INNER JOIN geographic_levels GLP ON GZP.levelId = GLP.id",
    "WHERE GZ.id = #{geographicZoneId}"})
  @Results(value = {
    @Result(property = "level.id", column = "levelId"),
    @Result(property = "level.code", column = "levelCode"),
    @Result(property = "level.name", column = "level"),
    @Result(property = "level.levelNumber", column = "levelNumber" ),
    @Result(property = "parent.name", column = "parentZone"),
    @Result(property = "parent.code", column = "parentCode"),
    @Result(property = "parent.level.code", column = "parentLevelCode"),
    @Result(property = "parent.level.name", column = "parentLevel")
  })
  GeographicZone getWithParentById(Long geographicZoneId);

  @Update({"UPDATE geographic_zones set code = #{code}, name = #{name}, levelId = #{level.id}, parentId = #{parent.id}, " +
    "catchmentPopulation = #{catchmentPopulation}, longitude = #{longitude}, latitude = #{latitude}, " +
    "modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate}",
    "WHERE id = #{id}"})
  void update(GeographicZone geographicZone);
}
