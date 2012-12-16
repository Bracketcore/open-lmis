package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramProductMapper {

    @Select("INSERT INTO program_products(programId, productId, dosesPerMonth, active, modifiedBy, modifiedDate)" +
            "VALUES ((select id from program where LOWER(code)=  LOWER(#{program.code}))," +
            "(select id from product where LOWER(code)=  LOWER(#{product.code})), " +
            "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate}) returning id")
    Integer insert(ProgramProduct programProduct);

    @Select("SELECT p.id AS product_id, p.code AS productCode, #{programCode} AS programCode, p.primary_name AS primary_name, " +
            "p.dispensing_unit, p.dosage_unit_id, " +
            "p.form_id, p.strength, p.doses_per_dispensing_unit, " +
            "pf.code AS form_code, pf.display_order AS form_display_order, " +
            "du.code AS dosage_unit_code, du.display_order AS dosage_unit_display_order, " +
            "pp.dosesPerMonth " +
            "from product p, facility_approved_product fap, program_products pp, facilities f, " +
            "product_form pf , dosage_unit du where " +
            "pp.programId = (select id from program where LOWER(code) =  LOWER(#{programCode})) " +
            "AND f.id = #{facilityId} AND f.typeId = fap.facility_type_id " +
            "AND fap.product_id = p.id " +
            "AND fap.product_id = pp.productId " +
            "AND pp.productId = p.id " +
            "AND pf.id = p.form_id " +
            "AND du.id = p.dosage_unit_id " +
            "AND p.full_supply = 'TRUE' " +
            "AND p.active = true " +
            "AND pp.active = true " +
            "ORDER BY p.display_order NULLS LAST, p.code")
    @Results(value = {
            @Result(property = "product", column = "product_id", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getProductById")),
            @Result(property = "product.code", column = "product_code"),
            @Result(property = "product.primaryName", column = "primary_name"),
            @Result(property = "product.dispensingUnit", column = "dispensing_unit"),
            @Result(property = "product.dosesPerDispensingUnit", column = "doses_per_dispensing_unit"),
            @Result(property = "product.strength", column = "strength"),
            @Result(property = "product.form.id", column = "form_id"),
            @Result(property = "product.form.code", column = "form_code"),
            @Result(property = "product.form.displayOrder", column = "form_display_order"),
            @Result(property = "product.dosageUnit.id", column = "dosage_unit_id"),
            @Result(property = "product.dosageUnit.code", column = "dosage_unit_code"),
            @Result(property = "product.dosageUnit.displayOrder", column = "dosage_unit_display_order")
    })
    List<ProgramProduct> getFullSupplyProductsByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                                   @Param("programCode") String programCode);
}