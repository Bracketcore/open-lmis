package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMapper {

    @Insert("INSERT INTO products (" +
            "code, " +
            "alternateItemCode," +
            "manufacturer," + "manufacturerCode," + "manufacturerBarcode," +
            "mohBarcode," +
            "gtin," +
            "type," +
            "displayOrder," +
            "primaryName," + "fullName," + "genericName," + "alternateName," + "description," +
            "strength," +
            "formId," +
            "dosageUnitId, dispensingUnit, dosesPerDispensingUnit," +
            "packSize," + "alternatePackSize," +
            "storeRefrigerated," + "storeRoomTemperature," + "hazardous," + "flammable," + "controlledSubstance," + "lightSensitive," + "approvedByWho," +
            "contraceptiveCyp," +
            "packLength," + "packWidth," + "packHeight," + "packWeight," + "packsPerCarton," +
            "cartonLength," + "cartonWidth," + "cartonHeight," + "cartonsPerPallet," +
            "expectedShelfLife," +
            "specialStorageInstructions," + "specialTransportInstructions," +
            "active," + "fullSupply," + "tracer," + "roundToZero," + "archived," +
            "packRoundingThreshold," +
            "modifiedBy, modifiedDate)" +
            "VALUES(" +
            "#{code}," +
            "#{alternateItemCode}," +
            "#{manufacturer}," + "#{manufacturerCode}," + "#{manufacturerBarCode}," +
            "#{mohBarCode}," +
            "#{gtin}," +
            "#{type}," +
            "#{displayOrder}," +
            "#{primaryName}," + "#{fullName}," + "#{genericName}," + "#{alternateName}," + "#{description}," +
            "#{strength}," +
            "#{form.id}, " +
            "#{dosageUnit.id}," +
            " #{dispensingUnit}, #{dosesPerDispensingUnit}," +
            "#{packSize}," + "#{alternatePackSize}," +
            "#{storeRefrigerated}," + "#{storeRoomTemperature}," + "#{hazardous}," + "#{flammable}," + "#{controlledSubstance}," + "#{lightSensitive}," + "#{approvedByWHO}," +
            "#{contraceptiveCYP}," +
            "#{packLength}," + "#{packWidth}," + "#{packHeight}," + "#{packWeight}," + "#{packsPerCarton}," +
            "#{cartonLength}," + "#{cartonWidth}," + "#{cartonHeight}," + "#{cartonsPerPallet}," +
            "#{expectedShelfLife}," +
            "#{specialStorageInstructions}," + "#{specialTransportInstructions}," +
            "#{active}," + "#{fullSupply}," + "#{tracer}," + "#{roundToZero}," + "#{archived}," +
            "#{packRoundingThreshold}," +
            "#{modifiedBy}, #{modifiedDate})")
    @Options(useGeneratedKeys = true)
    Integer insert(Product product);

    @Select("SELECT id FROM dosage_Unit where LOWER(code) = LOWER(#{code})")
    Integer getDosageUnitIdForCode(String code);

    @Select("SELECT id FROM product_form where LOWER(code) = LOWER(#{code})")
    Integer getProductFormIdForCode(String code);

    // Used by ProgramProductMapper
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product getById(Integer id);

    @Select("SELECT id from products where code = #{code}")
    Integer getIdByCode(String code);
}
