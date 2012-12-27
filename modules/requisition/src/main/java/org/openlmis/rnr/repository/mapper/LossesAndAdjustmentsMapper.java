package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface LossesAndAdjustmentsMapper {

    @Select("INSERT INTO requisition_line_item_losses_adjustments(requisitionLineItemId, lossesAdjustmentsType, quantity) " +
            "VALUES(#{rnrLineItem.id}, #{lossesAndAdjustments.type.name}, #{lossesAndAdjustments.quantity}) RETURNING id")
    @Options(useGeneratedKeys = true)
    public Integer insert(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "lossesAndAdjustments") LossesAndAdjustments lossesAndAdjustments);


    @Select("select * from requisition_line_item_losses_adjustments where requisitionLineItemId = #{id}")
    List<LossesAndAdjustments> getByRequisitionLineItem(RnrLineItem rnrLineItem);
}
