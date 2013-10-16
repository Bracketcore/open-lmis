package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class EDIFileColumn extends BaseModel {
  protected String name;
  protected String dataFieldLabel;
  protected Boolean include;
  protected Boolean mandatory;
  protected Integer position;
  protected String datePattern;
}
