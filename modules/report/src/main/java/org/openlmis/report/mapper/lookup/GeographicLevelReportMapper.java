package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.openlmis.core.domain.GeographicLevel;

import java.util.List;

/**
 * e-lmis
 * Created by: Henok Getachew
 * Date: Jun 18, 2013
 * Time: 11:44 AM
 */
@Repository
public interface GeographicLevelReportMapper {

    @Select("SELECT id,code,name " +
            "   FROM " +
            "       geographic_levels order by name")
    List<GeographicLevel> getAll();
}
