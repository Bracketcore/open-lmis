package org.openlmis.rnr.dto;

import org.junit.Test;
import org.openlmis.rnr.domain.Order;
import org.openlmis.rnr.domain.OrderBatch;
import org.openlmis.rnr.domain.Rnr;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;

public class RnrDTOTest {
  @Test
  public void shouldPrepareRequisitionsForApproval() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForListApproval(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getSubmittedDate(), is(rnr.getSubmittedDate()));
    assertThat(rnrDTO.getModifiedDate(), is(rnr.getModifiedDate()));
    assertThat(rnrDTO.getPeriodStartDate(), is(rnr.getPeriod().getStartDate()));
    assertThat(rnrDTO.getPeriodEndDate(), is(rnr.getPeriod().getEndDate()));
    assertThat(rnrDTO.getSupplyingDepot(), is(rnr.getSupplyingFacility().getName()));
  }

  @Test
  public void shouldPrepareRequisitionsForView() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForView(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getSubmittedDate(), is(rnr.getSubmittedDate()));
    assertThat(rnrDTO.getModifiedDate(), is(rnr.getModifiedDate()));
    assertThat(rnrDTO.getPeriodStartDate(), is(rnr.getPeriod().getStartDate()));
    assertThat(rnrDTO.getPeriodEndDate(), is(rnr.getPeriod().getEndDate()));
    assertThat(rnrDTO.getStatus(), is(rnr.getStatus().name()));
  }

  @Test
  public void shouldPrepareRequisitionsForOrderBatch() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    Date orderDate = new Date();
    OrderBatch orderBatch = new OrderBatch(1, orderDate, null, null);
    rnr.setOrder(new Order(null, orderBatch));
    List<Rnr> rnrList = Arrays.asList(rnr);
    List<RnrDTO> rnrDTOs = RnrDTO.prepareForOrderBatch(rnrList);

    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertThat(rnrDTO.getOrderBatchId(),is(rnr.getOrder().getOrderBatch().getId()));
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getOrderDate(), is(rnr.getOrder().getOrderBatch().getCreateTimeStamp()));
    assertThat(rnrDTO.getPeriodStartDate(), is(rnr.getPeriod().getStartDate()));
    assertThat(rnrDTO.getPeriodEndDate(), is(rnr.getPeriod().getEndDate()));
  }
}
