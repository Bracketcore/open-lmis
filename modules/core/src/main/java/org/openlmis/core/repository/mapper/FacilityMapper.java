package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMapper {

  @Insert("Insert into facilities(code, name, description, gln, mainPhone, fax, address1, address2, " +
    "geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById," +
    "coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, online," +
    "satellite, satelliteParentCode, hasElectricity, hasElectronicScc, hasElectronicDar, active," +
    "goLiveDate, goDownDate, comment, dataReportable, modifiedBy, modifiedDate) " +
    "values(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
    "#{geographicZone.id}," +
    "#{facilityType.id}," +
    "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
    "#{operatedBy.id}," +
    "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
    "#{satellite}, #{satelliteParentCode}, #{hasElectricity}, #{hasElectronicScc}, #{hasElectronicDar}, #{active}," +
    "#{goLiveDate}, #{goDownDate}, #{comment}, #{dataReportable}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(Facility facility);

  @Select("SELECT id, code, name FROM facilities")
  List<Facility> getAll();

  @Select("SELECT * FROM users U, facilities F " +
    "WHERE U.facilityId = F.id AND U.id = #{userId} AND f.active = true")
  @Results(value = {@Result(property = "id", column = "facilityId")})
  Facility getHomeFacility(Integer userId);

  @Select("SELECT * FROM facility_types ORDER BY displayOrder")
  List<FacilityType> getAllTypes();

  @Select("SELECT * FROM facility_types where id = #{id}")
  public FacilityType getFacilityTypeById(Integer id);

  @Select("SELECT * FROM facility_operators ORDER BY displayOrder")
  List<FacilityOperator> getAllOperators();

  @Select("SELECT * FROM facility_operators WHERE id = #{id}")
  public FacilityOperator getFacilityOperatorById(Integer id);

  @Select("SELECT code FROM facility_operators where id = #{id}")
  @SuppressWarnings("unused")
  public String getFacilityOperatorCodeFor(Integer id);

  @Select("SELECT id FROM facility_operators where LOWER(code) = LOWER(#{code})")
  Integer getOperatedByIdForCode(String code);


  @Select("SELECT GZ.id as id, GZ.name as name, GL.name as level FROM geographic_zones GZ, geopolitical_levels GL where GZ.level = GL.id")
  @Results(value = {
    @Result(property = "level.name", column = "level")
  })
  List<GeographicZone> getAllGeographicZones();

  @Select("SELECT * FROM facilities WHERE id = #{id}")
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class, one = @One(select = "getGeographicZoneById")),
    @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
  })
  Facility getById(Integer id);

  @Update("UPDATE facilities SET code = #{code}, name = #{name}, description = #{description}, gln = #{gln}," +
    "mainPhone = #{mainPhone}, fax = #{fax}, address1 = #{address1}," +
    "address2 = #{address2}, geographicZoneId = #{geographicZone.id}," +
    "typeId = #{facilityType.id}, catchmentPopulation = #{catchmentPopulation}, latitude = #{latitude}," +
    "longitude = #{longitude}, altitude = #{altitude}," +
    "operatedById = #{operatedBy.id}," +
    "coldStorageGrossCapacity = #{coldStorageGrossCapacity}, coldStorageNetCapacity = #{coldStorageNetCapacity}," +
    "suppliesOthers = #{suppliesOthers}, sdp = #{sdp}, online = #{online}, satellite = #{satellite}, satelliteParentCode = #{satelliteParentCode}," +
    "hasElectricity = #{hasElectricity}, hasElectronicScc = #{hasElectronicScc}, hasElectronicDar = #{hasElectronicDar}, active = #{active}," +
    "goLiveDate = #{goLiveDate}, goDownDate = #{goDownDate}," +
    "comment = #{comment}, dataReportable = #{dataReportable}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT id FROM facility_types where LOWER(code) = LOWER(#{code})")
  Integer getFacilityTypeIdForCode(String facilityTypeCode);

  @Update("UPDATE facilities SET dataReportable = #{dataReportable}, active=#{active}, modifiedBy=#{modifiedBy}, modifiedDate= DEFAULT " +
    "WHERE id =#{id}")
  void updateDataReportableAndActiveFor(Facility facility);

  @Select("SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{code})")
  Integer getIdForCode(String code);

  @Select("select 0<(select count(id) as count from geographic_zones where id=#{geographicZoneId})")
  Boolean isGeographicZonePresent(Integer geographicZoneId);

  @Select("SELECT DISTINCT f.* FROM facilities f " +
    "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
    "WHERE ps.programId = #{programId} " +
    "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND f.active = true " +
    "AND ps.active = true ")
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getFacilitiesBy(@Param(value = "programId") Integer programId, @Param(value = "requisitionGroupIds") String requisitionGroupIds);

  @Select("SELECT id, code, name FROM facilities WHERE " +
    "LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%' " +
    "OR LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%'")
  List<Facility> searchFacilitiesByCodeOrName(String searchParam);

  @Select({"SELECT GZ.id AS id, GZ.name AS name, GL.name AS level, GZP.name AS parentZone, GLP.name AS parentLevel",
    "FROM geographic_zones GZ INNER JOIN geographic_zones GZP ON GZ.parent = GZP.id",
    "INNER JOIN geopolitical_levels GL ON GZ.level = GL.id",
    "INNER JOIN geopolitical_levels GLP ON GZP.level = GLP.id",
    "WHERE GZ.id = #{geographicZoneId}"})
  @Results(value = {
    @Result(property = "level.name", column = "level"),
    @Result(property = "parent.name", column = "parentZone"),
    @Result(property = "parent.level.name", column = "parentLevel")
  })
  GeographicZone getGeographicZoneById(Integer geographicZoneId);

  //TODO can be done better??
  @Select({"SELECT * FROM facilities WHERE id IN (",
    "(SELECT facilityId FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId",
      "WHERE RA.supervisoryNodeId IS NULL AND RA.userId = #{userId} AND RR.rightName = ANY(#{commaSeparatedRights}::VARCHAR[]))",
    "UNION",
    "(SELECT facilityId FROM supervisory_nodes SN INNER JOIN role_assignments RA ON SN.id = RA.supervisoryNodeId INNER JOIN role_rights RR ON RA.roleId = RR.roleId",
      "WHERE RA.supervisoryNodeId IS NOT NULL AND RA.userId = #{userId} AND RR.rightName = ANY(#{commaSeparatedRights}::VARCHAR[]))",
    ")"})
  List<Facility> getForUserAndRights(@Param(value = "userId") Integer userId, @Param(value = "commaSeparatedRights") String commaSeparatedRights);

}
