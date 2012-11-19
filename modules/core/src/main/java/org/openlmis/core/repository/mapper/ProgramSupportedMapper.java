package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProgramSupported;

import java.util.List;

public interface ProgramSupportedMapper {
    @Insert("INSERT INTO PROGRAMS_SUPPORTED(FACILITY_CODE, PROGRAM_CODE, ACTIVE, MODIFIED_BY, MODIFIED_DATE) VALUES(#{facilityCode},#{programCode},#{isActive},#{modifiedBy},#{modifiedDate})")
    void addSupportedProgram(ProgramSupported programSupported);

    @Delete("DELETE FROM PROGRAMS_SUPPORTED")
    void deleteProgramMappings();

    @Select("SELECT * FROM PROGRAMS_SUPPORTED WHERE FACILITY_CODE=#{facilityCode} AND PROGRAM_CODE=#{programCode}")
    @Results(value = {
            @Result(property = "facilityCode", column = "FACILITY_CODE"),
            @Result(property = "programCode", column = "PROGRAM_CODE"),
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "modifiedBy", column = "MODIFIED_BY"),
            @Result(property = "modifiedDate", column = "MODIFIED_DATE")})
    List<ProgramSupported> getBy(@Param("facilityCode") String facilityCode, @Param("programCode") String programCode);
}
