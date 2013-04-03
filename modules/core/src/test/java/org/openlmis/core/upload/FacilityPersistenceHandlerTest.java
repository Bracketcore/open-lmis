/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FacilityPersistenceHandlerTest {
  FacilityPersistenceHandler facilityPersistenceHandler;
  @Mock
  private FacilityService facilityService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    facilityPersistenceHandler = new FacilityPersistenceHandler(facilityService);

  }

  @Test
  public void shouldSaveFacility() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    Date currentTimestamp = new Date();
    Facility existing = new Facility();
    facilityPersistenceHandler.save(existing, facility, new AuditFields(1, currentTimestamp));
    assertThat(facility.getModifiedBy(), is(1));
    assertThat(facility.getModifiedDate(), is(currentTimestamp));
    verify(facilityService).save(facility);
  }


}
