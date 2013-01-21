package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramProductBuilder {

  public static final Property<ProgramProduct, Integer> programId = newProperty();
  public static final Property<ProgramProduct, Integer> productId = newProperty();
  public static final Property<ProgramProduct, Integer> dosagePerMonth = newProperty();

  private static Property<ProgramProduct, String> productCode = newProperty();
  private static Property<ProgramProduct, String> programCode = newProperty();
  public static final String PRODUCT_CODE = "productCode";
  public static final String PROGRAM_CODE = "programCode";
  public static final Instantiator<ProgramProduct> defaultProgramProduct = new Instantiator<ProgramProduct>() {

    @Override
    public ProgramProduct instantiate(PropertyLookup<ProgramProduct> lookup) {
      Product product = new Product();
      product.setId(lookup.valueOf(productId, 1));
      product.setCode(lookup.valueOf(productCode, PRODUCT_CODE));
      Program program = new Program();
      program.setId(lookup.valueOf(programId, 1));
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
      return new ProgramProduct(program, product, lookup.valueOf(dosagePerMonth, 1), true);
    }
  };
}
