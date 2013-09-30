/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrLineItemMapper {

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, productDisplayOrder, productCategory, productCategoryDisplayOrder, beginningBalance,",
    "quantityReceived, quantityDispensed, dispensingUnit,dosesPerMonth, dosesPerDispensingUnit, maxMonthsOfStock,",
    "totalLossesAndAdjustments, packsToShip, packSize, price, roundToZero, packRoundingThreshold, fullSupply,",
    "previousStockInHandAvailable,stockInHand, newPatientCount, stockOutDays,",
    "modifiedBy, modifiedDate, createdBy)",
    "VALUES (",
    "#{rnrId}, #{productCode}, #{product}, #{productDisplayOrder}, #{productCategory}, #{productCategoryDisplayOrder}, #{beginningBalance},",
    "#{quantityReceived}, #{quantityDispensed}, #{dispensingUnit},#{dosesPerMonth}, #{dosesPerDispensingUnit}, #{maxMonthsOfStock},",
    "#{totalLossesAndAdjustments}, #{packsToShip}, #{packSize}, #{price},#{roundToZero}, #{packRoundingThreshold}, #{fullSupply},",
    "#{previousStockInHandAvailable}, #{stockInHand}, #{newPatientCount}, #{stockOutDays},",
    "#{modifiedBy}, #{modifiedDate}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  public Integer insert(RnrLineItem rnrLineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} and fullSupply = true order by id")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "lossesAndAdjustments", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper.getByRnrLineItem"))
  })
  public List<RnrLineItem> getRnrLineItemsByRnrId(Long rnrId);

  @Update("UPDATE requisition_line_items " +
    "SET quantityReceived = #{quantityReceived}, " +
    " quantityDispensed = #{quantityDispensed}, " +
    " beginningBalance = #{beginningBalance}, " +
    " stockInHand = #{stockInHand}, " +
    " quantityRequested = #{quantityRequested}, " +
    " reasonForRequestedQuantity = #{reasonForRequestedQuantity}, " +
    " totalLossesAndAdjustments = #{totalLossesAndAdjustments}, " +
    " calculatedOrderQuantity = #{calculatedOrderQuantity}, " +
    " quantityApproved = #{quantityApproved}, " +
    " newPatientCount = #{newPatientCount}, " +
    " stockOutDays = #{stockOutDays}, " +
    " normalizedConsumption = #{normalizedConsumption}, " +
    " amc = #{amc}, " +
    " maxStockQuantity = #{maxStockQuantity}, " +
    " packsToShip = #{packsToShip}, " +
    " remarks = #{remarks}, " +
    " expirationDate = #{expirationDate}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = CURRENT_TIMESTAMP " +
    "WHERE id = #{id}"
  )
  int update(RnrLineItem rnrLineItem);

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, productDisplayOrder, productCategory, productCategoryDisplayOrder, dispensingUnit,",
    "dosesPerMonth, dosesPerDispensingUnit, maxMonthsOfStock, packSize, price, roundToZero,",
    "packRoundingThreshold, fullSupply, modifiedBy, quantityReceived, quantityDispensed, beginningBalance,",
    "stockInHand, totalLossesAndAdjustments, calculatedOrderQuantity, quantityApproved,",
    "newPatientCount, stockOutDays, normalizedConsumption, amc, maxStockQuantity,",
    "remarks, quantityRequested, reasonForRequestedQuantity)",
    "VALUES ( ",
    "#{rnrId}, #{productCode}, #{product}, #{productDisplayOrder}, #{productCategory}, #{productCategoryDisplayOrder}, #{dispensingUnit},",
    "#{dosesPerMonth}, #{dosesPerDispensingUnit}, #{maxMonthsOfStock},#{packSize}, #{price}, #{roundToZero},",
    "#{packRoundingThreshold}, #{fullSupply}, #{modifiedBy}, 0, 0, 0,",
    "0, 0, 0, #{quantityApproved},",
    "0, 0, 0, 0, 0,",
    " #{remarks}, #{quantityRequested}, #{reasonForRequestedQuantity})"})
  @Options(useGeneratedKeys = true)
  void insertNonFullSupply(RnrLineItem requisitionLineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND fullSupply = false")
  public List<RnrLineItem> getNonFullSupplyRnrLineItemsByRnrId(Long rnrId);


  @Delete("DELETE FROM requisition_line_items WHERE rnrId = #{rnrId} AND fullSupply = false")
  void deleteAllNonFullSupplyForRequisition(Long rnrId);

  @Select(
    "SELECT COUNT(DISTINCT productCategory) FROM requisition_line_items WHERE rnrId=#{rnr.id} AND fullSupply = #{isFullSupply}")
  public Integer getCategoryCount(@Param(value = "rnr") Rnr rnr, @Param(value = "isFullSupply") Boolean isFullSupply);

  @Update("UPDATE requisition_line_items " +
    "SET quantityApproved = #{quantityApproved}, " +
    " packsToShip = #{packsToShip}, " +
    " remarks = #{remarks}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = CURRENT_TIMESTAMP " +
    " WHERE id = #{id}"
  )
  void updateOnApproval(RnrLineItem lineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND productCode = #{productCode} AND fullSupply = false")
  RnrLineItem getExistingNonFullSupplyItemByRnrIdAndProductCode(@Param(value = "rnrId") Long rnrId, @Param(value = "productCode") String productCode);
}
