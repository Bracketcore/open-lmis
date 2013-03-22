/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;
import java.util.Set;

import static java.lang.Boolean.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  private Integer id;
  private String name;
  private Boolean adminRole;
  private String description;
  private Integer modifiedBy;
  private Date modifiedDate;
  private Set<Right> rights;

  public Role(String name, Boolean adminRole, String description) {
    this(null, name, adminRole, description);
  }

  public Role(Integer id, String name, Boolean adminRole, String description) {
    this(id, name, adminRole, description, null, null, null);
  }

  public void validate() {
    if (name == null || name.isEmpty()) throw new DataException("Role can not be created without name.");
    if (rights == null || rights.isEmpty())
      throw new DataException("Role can not be created without any rights assigned to it.");
  }
}
