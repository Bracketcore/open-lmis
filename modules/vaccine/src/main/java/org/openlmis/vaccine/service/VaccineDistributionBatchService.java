/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.InventoryBatch;
import org.openlmis.vaccine.domain.InventoryTransaction;
import org.openlmis.vaccine.repository.VaccineDistributionBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineDistributionBatchService {

    private final String TRANSACTION_TYPE_RECEIVED = "Received";
    private final String TRANSACTION_TYPE_ISSUED = "Issued";

    @Autowired
    private VaccineDistributionBatchRepository distributionBatchRepository;

    @Autowired
    private TransactionTypeService transactionTypeService;

    public List<InventoryTransaction> getReceivedVaccinesForFacility(Long facilityId){
        return distributionBatchRepository.getReceivedVaccinesForFacility(facilityId);
    }

    public InventoryTransaction getReceivedVaccinesById(Long id){
        return distributionBatchRepository.getReceivedVaccineById(id);
    }

    public List<InventoryBatch> getUsableBatches(Long productId){
        return  distributionBatchRepository.getUsableBatches(productId);
    }

    public void receiveVaccine(InventoryTransaction inventoryTransaction) {
        if(inventoryTransaction != null && inventoryTransaction.getId() == null){
            inventoryTransaction.setTransactionType(transactionTypeService.getByName(TRANSACTION_TYPE_RECEIVED));
        }
        distributionBatchRepository.updateInventoryTransaction(inventoryTransaction, true);
    }

    public void distributeVaccine(InventoryTransaction inventoryTransaction) {
        if(inventoryTransaction != null && inventoryTransaction.getId() == null){
            inventoryTransaction.setTransactionType(transactionTypeService.getByName(TRANSACTION_TYPE_ISSUED));
        }
        distributionBatchRepository.updateInventoryTransaction(inventoryTransaction,false);
    }

}
