package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EDIFileTemplate {
  protected EDIConfiguration configuration;
  protected List<EDIFileColumn> columns;
}
