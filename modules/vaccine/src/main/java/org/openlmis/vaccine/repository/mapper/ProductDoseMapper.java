/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDoseMapper {

  @Select("select pd.* from vaccine_product_doses pd join vaccine_doses d on d.id = pd.doseId where productId = #{productId} order by d.displayOrder")
  List<VaccineProductDose> getDoseSettingByProduct(@Param("productId")Long productId);

  @Select("select d.id as doseId, false as isActive,#{productId} as productId from vaccine_doses d order by d.displayOrder")
  List<VaccineProductDose> getEmptySettingByProduct(@Param("productId")Long productId);

  @Insert("insert into vaccine_product_doses (doseId, productId, isActive, createdBy, modifiedBy) " +
    " values " +
    " ( #{doseId}, #{productId}, #{isActive},#{createdBy}, #{modifiedBy} )")
  void insert(VaccineProductDose dose);

  @Update("update vaccine_product_doses " +
    " set " +
    " doseId = #{doseId}," +
    " productId = #{productId}, " +
    " isActive = #{isActive}," +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = CURRENT_TIMESTAMP" +
    " where id = #{id}")
  void update(VaccineProductDose dose);

}
