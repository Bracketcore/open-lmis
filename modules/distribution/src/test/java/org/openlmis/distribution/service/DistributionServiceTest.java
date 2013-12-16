/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.DistributionRepository;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionServiceTest {

  @InjectMocks
  DistributionService service;

  @Mock
  FacilityDistributionService facilityDistributionService;

  @Mock
  DistributionRepository repository;

  @Test
  public void shouldCreateDistribution() throws Exception {
    Distribution distribution = new Distribution();
    Distribution expectedDistribution = new Distribution();
    when(repository.create(distribution)).thenReturn(expectedDistribution);
    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();
    when(facilityDistributionService.getFor(expectedDistribution)).thenReturn(facilityDistributions);

    Distribution initiatedDistribution = service.create(distribution);

    verify(repository).create(distribution);
    assertThat(initiatedDistribution, is(expectedDistribution));
    assertThat(initiatedDistribution.getFacilityDistributions(), is(facilityDistributions));
  }

  @Test
  public void shouldSyncFacilityDistributionDataAndReturnSyncStatus() {
    FacilityVisit facilityVisit = new FacilityVisit();

    FacilityDistribution facilityDistribution = mock(FacilityDistribution.class);
    when(facilityDistribution.getFacilityVisit()).thenReturn(facilityVisit);
    when(facilityDistributionService.save(facilityDistribution)).thenReturn(true);
    boolean syncStatus = service.sync(facilityDistribution);

    verify(facilityDistributionService).save(facilityDistribution);
    assertTrue(syncStatus);
  }

  @Test
  public void shouldGetDistributionIfExists() throws Exception {
    Distribution distribution = new Distribution();

    service.get(distribution);

    verify(repository).get(distribution);
  }
}
