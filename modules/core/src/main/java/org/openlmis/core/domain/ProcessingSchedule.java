package org.openlmis.core.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProcessingSchedule {
    private Integer id;

    private String code;
    private String name;
    private String description;
    private Integer modifiedBy;
    private Date modifiedDate;

    public ProcessingSchedule(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public void validate() {
        if (code == null || code.isEmpty()) throw new RuntimeException("Schedule can not be saved without its code.");
        if (name == null || name.isEmpty())
            throw new RuntimeException("Schedule can not be saved without its name.");
    }
}
