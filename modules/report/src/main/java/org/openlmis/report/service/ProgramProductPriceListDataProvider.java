package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.lookup.ProgramProductPriceListMapper;
import org.openlmis.report.model.dto.ProgramProductPriceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mahmed
 * Date: 6/19/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates..
 */

@NoArgsConstructor
@Service
public class ProgramProductPriceListDataProvider {

    @Autowired
   private ProgramProductPriceListMapper programProductPriceListMapper;

    public List<ProgramProductPriceList> getByProductId(Long productId){
        return programProductPriceListMapper.getByProductId(productId);
    }

    public List<ProgramProductPriceList> getAllPrices(){
        return programProductPriceListMapper.getAllPrices();
    }

}
