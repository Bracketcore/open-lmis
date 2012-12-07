package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DosageUnit {

    private long id;
    private String code;
    private int displayOrder;
}
