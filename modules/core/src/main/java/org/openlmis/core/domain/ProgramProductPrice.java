package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProductPrice implements Importable {
  public static final String PROGRAM_PRODUCT_PRICE_INVALID_PRICE_PER_DOSAGE = "programProductPrice.invalid.price.per.dosage";

  private Integer id;

  @ImportFields(importFields = {
      @ImportField(name = "Program Code", type = "String", nested = "program.code", mandatory = true),
      @ImportField(name = "Product Code", type = "String", nested = "product.code", mandatory = true),
      @ImportField(name = "Price per pack", type = "BigDecimal", nested = "currentPrice", mandatory = true)
  })
  private ProgramProduct programProduct;

  @ImportField(name = "Price per dosage unit", type = "BigDecimal")
  private Money pricePerDosage;

  @ImportField(name = "Funding Source", type = "String")
  private String source;

  private Date startDate;
  private Date endDate;
  private Integer modifiedBy;

  public ProgramProductPrice(ProgramProduct programProduct, Money pricePerDosage, String source) {
    this.programProduct = programProduct;
    this.pricePerDosage = pricePerDosage;
    this.source = source;
  }

  public void validate() {
    programProduct.validate();
    if(pricePerDosage.isNegative()) throw new DataException(PROGRAM_PRODUCT_PRICE_INVALID_PRICE_PER_DOSAGE);
  }
}
