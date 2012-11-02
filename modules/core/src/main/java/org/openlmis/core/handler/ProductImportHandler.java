package org.openlmis.core.handler;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.RecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductImportHandler implements RecordHandler<Product> {

    private ProductService service;

    @Autowired
    public ProductImportHandler(ProductService service) {
        this.service = service;
    }

    @Override
    public void execute(Product product) {
        service.save(product);
    }
}
