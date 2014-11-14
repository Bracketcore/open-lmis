package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.StorageType;
import org.openlmis.vaccine.domain.Temprature;
import org.openlmis.vaccine.domain.VaccineStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
@Repository
public interface StorageTypeMapper {
    @Select("SELECT * FROM storage_types ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypename", property = "storageTypeName")
    })

    List<StorageType> loadAllList();
    @Insert({"INSERT INTO storage_types",
            "( storagetypename, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{tempratureName} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(StorageType storageType);

    @Select("SELECT * FROM storage_types where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypename", property = "storageTypeName")
    })
    StorageType getById(Long id);
    @Update("UPDATE storage_types " +
            "   SET storagetypename= #{storageTypeName}," +
                       " modifiedby=#{modifiedBy}, " +
            "modifieddate=#{modifiedDate} " +

            " WHERE id=#{id};")
    void update(StorageType storageType);

    @Delete("DELETE from storage_types " +
            " WHERE id=#{id};")
    void delete(StorageType storageType);
}
