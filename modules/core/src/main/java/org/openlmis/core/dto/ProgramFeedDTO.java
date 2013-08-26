/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.Program;

@Data
@EqualsAndHashCode(callSuper=false)
public class ProgramFeedDTO extends BaseFeedDTO{

  private String programCode;

  private String programName;

  public ProgramFeedDTO(Program program) {
    this.programCode = program.getCode();
    this.programName = program.getName();
  }
}
