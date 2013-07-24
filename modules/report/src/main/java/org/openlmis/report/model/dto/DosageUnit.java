package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:42 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DosageUnit {

    private Integer id;
    private Integer displayOrder;
    private String code;

}
