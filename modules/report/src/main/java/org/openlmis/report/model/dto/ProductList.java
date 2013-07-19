package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.domain.ProductCategory;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:42 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductList {

    private boolean active;
    private Integer categoryid;
    private String code;
    private String dispensingunit;
    private Integer displayorder ;
    private Integer dosageunitid ;
    private Integer formid;
    private String  fullname;
    private String  programname;
    private Boolean fullsupply;
    private Integer packsize;
    private String strength;
    private Boolean tracer;
    private String  type;
    private Integer packroundingthreshold;
    private String formcode;
    private String dosageunitcode;

}
