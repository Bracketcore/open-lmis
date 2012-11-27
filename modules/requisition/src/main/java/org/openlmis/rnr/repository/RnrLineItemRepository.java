package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class RnrLineItemRepository {

    private RnrLineItemMapper rnrLineItemMapper;

    @Autowired
    public RnrLineItemRepository(RnrLineItemMapper rnrLineItemMapper) {
        this.rnrLineItemMapper = rnrLineItemMapper;
    }

    public void insert(RnrLineItem requisitionLineItem) {
        rnrLineItemMapper.insert(requisitionLineItem);
    }

}
