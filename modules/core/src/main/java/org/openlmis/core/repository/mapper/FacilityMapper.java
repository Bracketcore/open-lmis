package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMapper {

    @Select("Insert into facility(code, name, description,gln,main_phone,fax,address1,address2, " +
            "geographic_zone_id,type_id,catchment_population,latitude,longitude,altitude,operated_by_id," +
            "cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp,is_online," +
            "is_satellite,satellite_parent_code,has_electricity,has_electronic_scc,has_electronic_dar,is_active," +
            "go_live_date,go_down_date,comment,data_reportable,modified_by,modified_date) " +
            "values(#{code}, #{name},#{description},#{gln},#{mainPhone},#{fax},#{address1}, #{address2}," +
            "#{geographicZone.id}," +
            "(select id from facility_type where LOWER(code) = LOWER(#{facilityType.code}))," +
            "#{catchmentPopulation},#{latitude},#{longitude},#{altitude}," +
            "(select id from facility_operator where LOWER(code) = LOWER(#{operatedBy.code}))," +
            "#{coldStorageGrossCapacity},#{coldStorageNetCapacity},#{suppliesOthers},#{sdp},#{online}," +
            "#{satellite},#{satelliteParentCode},#{hasElectricity},#{hasElectronicScc},#{hasElectronicDar},#{active}," +
            "#{goLiveDate},#{goDownDate},#{comment},#{dataReportable},#{modifiedBy},#{modifiedDate}) returning id")
    @Options(useGeneratedKeys = true)
    int insert(Facility facility);

    @Select("SELECT id, code, name, description,gln,main_phone,fax,address1,address2," +
            "geographic_zone_id,type_id,catchment_population,latitude,longitude,altitude,operated_by_id," +
            "cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp,is_online," +
            "is_satellite,satellite_parent_code,has_electricity,has_electronic_scc,has_electronic_dar,is_active," +
            "go_live_date,go_down_date,comment,data_reportable,modified_by,modified_date FROM FACILITY")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "gln", column = "gln"),
            @Result(property = "mainPhone", column = "main_phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "address1", column = "address1"),
            @Result(property = "address2", column = "address2"),
            @Result(property = "geographicZone.id", column = "geographic_zone_id"),
            @Result(property = "facilityType.code", column = "type_id",javaType = java.lang.String.class, one = @One(select = "getFacilityTypeCodeFor") ),
            @Result(property = "catchmentPopulation", column = "catchment_population"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "altitude", column = "altitude"),
            @Result(property = "operatedBy.code", column = "operated_by_id",javaType = java.lang.String.class, one = @One(select = "getFacilityOperatorCodeFor")),
            @Result(property = "coldStorageGrossCapacity", column = "cold_storage_gross_capacity"),
            @Result(property = "coldStorageNetCapacity", column = "cold_storage_net_capacity"),
            @Result(property = "suppliesOthers", column = "supplies_others"),
            @Result(property = "sdp", column = "is_sdp"),
            @Result(property = "online", column = "is_online"),
            @Result(property = "satellite", column = "is_satellite"),
            @Result(property = "satelliteParentCode", column = "satellite_parent_code"),
            @Result(property = "hasElectricity", column = "has_electricity"),
            @Result(property = "hasElectronicScc", column = "has_electronic_scc"),
            @Result(property = "active", column = "is_active"),
            @Result(property = "goLiveDate", column = "go_live_date"),
            @Result(property = "goDownDate", column = "go_down_date"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "dataReportable", column = "data_reportable"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    List<Facility> getAll();

    @Delete("DELETE FROM FACILITY")
    void deleteAll();

    @Select("SELECT * FROM users U, facility F " +
            "where U.facility_id = F.id and U.user_name = #{userName}")
    @Results(value = {
            @Result(property = "id", column = "facility_id"),
            @Result(property = "name"),
            @Result(property = "code")
    })
    Facility getHomeFacility(String userName);

    @Select("SELECT F.name, F.code,F.operated_by_id as operated_by_id, FT.name as facility_type, FT.nominal_max_month, " +
            "FT.nominal_eop, GZ.name as zone, GL.name as label, GZP.name as parent_zone, GLP.name as parent_label " +
            "FROM facility F, facility_type FT, geographic_zone GZ, geographic_zone GZP, geopolitical_level GL, geopolitical_level GLP " +
            "WHERE F.id = #{facilityId} AND " +
            "F.type_id = FT.id AND " +
            "F.geographic_zone_id = GZ.id AND " +
            "GZ.parent = GZP.id AND " +
            "GZ.level = GL.id AND " +
            "GZP.level = GLP.id")
    @Results(value = {
            @Result(property = "facilityName", column = "name"),
            @Result(property = "facilityCode", column = "code"),
            @Result(property = "facilityOperatedBy", column = "operated_by_id", javaType = java.lang.String.class, one = @One(select = "getFacilityOperatorCodeFor")),
            @Result(property = "facilityType", column = "facility_type"),
            @Result(property = "maximumStockLevel", column = "nominal_max_month"),
            @Result(property = "emergencyOrderPoint", column = "nominal_eop"),
            @Result(property = "zone.value", column = "zone"),
            @Result(property = "zone.label", column = "label"),
            @Result(property = "parentZone.value", column = "parent_zone"),
            @Result(property = "parentZone.label", column = "parent_label")
    })
    RequisitionHeader getRequisitionHeaderData(int facilityId);


    @Select("SELECT * FROM facility_type ORDER BY display_order")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "levelId", column = "level_id"),
            @Result(property = "nominalMaxMonth", column = "nominal_max_month"),
            @Result(property = "nominalEop", column = "nominal_eop"),
            @Result(property = "displayOrder", column = "display_order"),
            @Result(property = "active", column = "is_active")
    })
    List<FacilityType> getAllTypes();

    @Select("SELECT code FROM facility_type where id = #{id}")
    public String getFacilityTypeCodeFor(Integer id);

    @Select("SELECT * FROM facility_operator ORDER BY display_order")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "text", column = "text"),
            @Result(property = "displayOrder", column = "display_order")
    })
    List<FacilityOperator> getAllOperators();

    @Select("SELECT code FROM facility_operator where id = #{id}")
    public String getFacilityOperatorCodeFor(Integer id);

    @Select("SELECT GZ.id as id, GZ.name as value, GL.name as label FROM geographic_zone GZ, geopolitical_level GL where GZ.level = GL.id")
    List<GeographicZone> getAllGeographicZones();

    @Select("SELECT * FROM FACILITY WHERE id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "gln", column = "gln"),
            @Result(property = "mainPhone", column = "main_phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "address1", column = "address1"),
            @Result(property = "address2", column = "address2"),
            @Result(property = "geographicZone.id", column = "geographic_zone_id"),
            @Result(property = "facilityType.code", column = "type_id"),
            @Result(property = "catchmentPopulation", column = "catchment_population"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "altitude", column = "altitude"),
            @Result(property = "operatedBy.code", column = "operated_by_id"),
            @Result(property = "coldStorageGrossCapacity", column = "cold_storage_gross_capacity"),
            @Result(property = "coldStorageNetCapacity", column = "cold_storage_net_capacity"),
            @Result(property = "suppliesOthers", column = "supplies_others"),
            @Result(property = "sdp", column = "is_sdp"),
            @Result(property = "online", column = "is_online"),
            @Result(property = "satellite", column = "is_satellite"),
            @Result(property = "satelliteParentCode", column = "satellite_parent_code"),
            @Result(property = "hasElectricity", column = "has_electricity"),
            @Result(property = "hasElectronicScc", column = "has_electronic_scc"),
            @Result(property = "hasElectronicDar", column = "has_electronic_dar"),
            @Result(property = "active", column = "is_active"),
            @Result(property = "goLiveDate", column = "go_live_date"),
            @Result(property = "goDownDate", column = "go_down_date"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "dataReportable", column = "data_reportable"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    Facility get(int id);


    @Update("UPDATE facility SET code=#{code},name=#{name},description=#{description},gln=#{gln},main_phone=#{mainPhone},fax=#{fax},address1=#{address1}," +
            "address2=#{address2},geographic_zone_id=#{geographicZone.id}," +
            "type_id=(select id from facility_type where LOWER(code) = LOWER(#{facilityType.code})),catchment_population=#{catchmentPopulation},latitude=#{latitude}," +
            "longitude=#{longitude},altitude=#{altitude}," +
            "operated_by_id=(select id from facility_operator where LOWER(code)= LOWER(#{operatedBy.code}))," +
            "cold_storage_gross_capacity=#{coldStorageGrossCapacity},cold_storage_net_capacity=#{coldStorageNetCapacity}," +
            "supplies_others=#{suppliesOthers},is_sdp=#{sdp},is_online=#{online},is_satellite=#{satellite},satellite_parent_code=#{satelliteParentCode}," +
            "has_electricity=#{hasElectricity}," +
            "has_electronic_scc=#{hasElectronicScc},has_electronic_dar=#{hasElectronicDar},is_active=#{active},go_live_date=#{goLiveDate},go_down_date=#{goDownDate}," +
            "comment=#{comment},data_reportable=#{dataReportable},modified_by=#{modifiedBy},modified_date=#{modifiedDate} WHERE id=#{id}")
    void update(Facility facility);

    Integer getIdForCode(String code);
}
