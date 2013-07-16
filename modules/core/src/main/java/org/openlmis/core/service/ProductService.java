package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductGroupRepository;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class ProductService {

  private ProductRepository repository;
  private ProductGroupRepository productGroupRepository;
  private ProductCategoryService categoryService;

  @Autowired
  public ProductService(ProductRepository repository, ProductCategoryService categoryService, ProductGroupRepository productGroupRepository) {
    this.repository = repository;
    this.categoryService = categoryService;
    this.productGroupRepository = productGroupRepository;
  }

  public void save(Product product) {
    validateAndSetProductCategory(product);

    if (product.getId() == null) {
      repository.insert(product);
      return;
    }

    setReferenceDataForProduct(product);

    repository.update(product);
  }


  private void setReferenceDataForProduct(Product product) {
    if (product.getForm() != null) {
      product.getForm().setId(repository.getProductFormIdForCode(product.getForm().getCode()));
    }
    if (product.getDosageUnit() != null) {
      product.getDosageUnit().setId(repository.getDosageUnitIdForCode(product.getDosageUnit().getCode()));
    }
    if (product.getProductGroup() != null) {
      ProductGroup productGroup = productGroupRepository.getByCode(product.getProductGroup().getCode());
      if (productGroup == null) throw new DataException("error.reference.data.invalid.product.group");
      product.getProductGroup().setId(productGroup.getId());

    }
  }


  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Long categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException("error.reference.data.invalid.product");
    }
    category.setId(categoryId);
  }

  public Long getIdForCode(String code) {
    return repository.getIdByCode(code);
  }

  public Product getByCode(String code) {
    return repository.getByCode(code);
  }

   // mahmed 07.11.2013 full product list
    public List<Product> getProductsList() {
        return repository.getProductList();
    }

    //mahmed - 07.11.2013 delete product
    public void deleteById(Long productId) {

        this.repository.deleteById(productId);
    }

    //mahmed - 07.11.2013 delete product
    public void restoreById(Long productId) {

        this.repository.restoreById(productId);
    }
    //mahmed - 07.11.2013 delete product
    public Product get(Long id) {
        Product product = repository.get(id);
        if (product == null) {
            throw new DataException("error.supplyline.not.found");
        }
        return product;
    }
 }
