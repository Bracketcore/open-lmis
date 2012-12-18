package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityApprovedProductRepository {

    private FacilityApprovedProductMapper facilityApprovedProductMapper;

    @Autowired
    public FacilityApprovedProductRepository(FacilityApprovedProductMapper facilityApprovedProductMapper) {
        this.facilityApprovedProductMapper = facilityApprovedProductMapper;
    }

    public List<FacilityApprovedProduct> getByFacilityAndProgram(Integer facilityId, String programCode) {
        return facilityApprovedProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programCode);
    }

}
