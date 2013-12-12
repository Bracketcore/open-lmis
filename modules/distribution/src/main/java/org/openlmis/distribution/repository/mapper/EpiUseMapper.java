/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.springframework.stereotype.Repository;

@Repository
public interface EpiUseMapper {

  @Insert({"INSERT into epi_use_line_items (epiUseId, productGroupId, productGroupName, stockAtFirstOfMonth, received, ",
    "distributed, loss, stockAtEndOfMonth, expirationDate) VALUES (#{epiUseId}, #{productGroup.id}, #{productGroup.name}, #{stockAtFirstOfMonth},",
    " #{received}, #{distributed}, #{loss}, #{stockAtEndOfMonth}, #{expirationDate})"})
  @Options(useGeneratedKeys = true)
  public void insertLineItem(EpiUseLineItem epiUseLineItem);

  @Insert({"INSERT INTO epi_use (distributionId, facilityId) VALUES (#{distributionId}, #{facilityId})"})
  @Options(useGeneratedKeys = true)
  public void insert(EpiUse epiUse);

}
