package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ProductServiceExtension {

     @Autowired
    ProductRepositoryExtension repository;

      public List<Product> getProductsList() {
        return repository.getProductList();
    }


}
