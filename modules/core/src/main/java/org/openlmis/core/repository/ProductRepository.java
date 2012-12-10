package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProductRepository {

    ProductMapper mapper;

    @Autowired
    public ProductRepository(ProductMapper mapper) {
        this.mapper = mapper;
    }

    public void insert(Product product) {
        try {
            mapper.insert(product);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException("Duplicate Product Code found");
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
            if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
                throw new RuntimeException("Missing/Invalid Reference data");
            }else{
                throw new RuntimeException("Incorrect data length");
            }
        }
    }

    public List<Product> getByFacilityAndProgram(int facilityId, String programCode) {
        return mapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programCode);
    }

}
