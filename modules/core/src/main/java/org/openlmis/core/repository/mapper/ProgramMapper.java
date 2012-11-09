package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;

import java.util.List;

public interface ProgramMapper {

    @Select("SELECT * FROM program WHERE active=true")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION")
    })
    List<Program> getAllActive();

    @Insert("INSERT INTO PROGRAM(ID, NAME, DESCRIPTION)" +
            " values (#{program.id}, #{program.name}, #{program.description})")
    int insert(@Param("program") Program program);

    @Delete("DELETE FROM PROGRAM")
    void deleteAll();


    @Select("select * from program P, programs_supported PS where P.id = PS.program_id and PS.facility_code = #{facilityCode} and PS.active=true")
    @Results(value = {
            @Result(property = "id", column = "program.id"),
            @Result(property = "name", column = "program.name"),
            @Result(property = "description", column = "program.description")
    })
    List<Program> getActiveByFacilityCode(String facilityCode);
}