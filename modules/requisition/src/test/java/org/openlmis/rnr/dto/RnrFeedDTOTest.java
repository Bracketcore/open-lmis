/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.dto;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.Vendor;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
@Category(UnitTests.class)
public class RnrFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    Vendor vendor = new Vendor();
    vendor.setName("external system");
    RnrFeedDTO feed = RnrFeedDTO.populate(rnr, vendor);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(feed.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(feed.getPeriodId(), is(rnr.getPeriod().getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.getExternalSystem(), is(vendor.getName()));
  }

  @Test
  public void shouldGetSerializedContentsFromRequisition() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Rnr requisition = make(a(defaultRnr));
    Vendor vendor = new Vendor();
    RnrFeedDTO feedDTO = RnrFeedDTO.populate(requisition, vendor);

    String serializedContents = feedDTO.getSerializedContents();

    assertThat(serializedContents, is(mapper.writeValueAsString(feedDTO)));
  }
}
