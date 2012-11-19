package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Program {

    private Integer id;
    private String code;
    private String name;
    private String description;
    private boolean active;

    public Program() {
    }
}
