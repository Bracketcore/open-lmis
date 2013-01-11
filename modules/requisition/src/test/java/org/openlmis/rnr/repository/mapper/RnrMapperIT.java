package org.openlmis.rnr.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RnrMapperIT {

  public static final int MODIFIED_BY = 1;
  public static final Integer HIV = MODIFIED_BY;
  public static final int USER_2 = 2;
  Facility facility;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private RnrMapper rnrMapper;
  private Rnr requisition;

  @Before
  public void setUp() {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    requisition = new Rnr(facility.getId(), HIV, MODIFIED_BY);
    requisition.setStatus(INITIATED);
  }

  @Test
  public void shouldSetRequisitionId() {
    rnrMapper.insert(requisition);
    assertThat(requisition.getId(), is(notNullValue()));
  }

  @Test
  public void shouldReturnRequisitionById() {
    rnrMapper.insert(requisition);
    Rnr fetchedRequisition = rnrMapper.getRequisitionById(requisition.getId());
    assertThat(fetchedRequisition.getId(), is(requisition.getId()));
    assertThat(fetchedRequisition.getProgramId(), is(equalTo(HIV)));
    assertThat(fetchedRequisition.getFacilityId(), is(equalTo(facility.getId())));
    assertThat(fetchedRequisition.getModifiedBy(), is(equalTo(MODIFIED_BY)));
    assertThat(fetchedRequisition.getStatus(), is(equalTo(INITIATED)));
  }

  @Test
  public void shouldUpdateRequisition() {
    rnrMapper.insert(requisition);
    requisition.setModifiedBy(USER_2);
    requisition.setFullSupplyItemsSubmittedCost(100.5F);
    requisition.setTotalSubmittedCost(100.5F);

    rnrMapper.update(requisition);

    Rnr updatedRequisition = rnrMapper.getRequisitionById(requisition.getId());

    assertThat(updatedRequisition.getId(), is(requisition.getId()));
    assertThat(updatedRequisition.getModifiedBy(), is(equalTo(USER_2)));
    assertThat(updatedRequisition.getFullSupplyItemsSubmittedCost(), is(100.5F));
    assertThat(updatedRequisition.getTotalSubmittedCost(), is(100.5F));
  }

  @Test
  public void shouldReturnRequisitionByFacilityAndProgramAndIfExists() {
    rnrMapper.insert(requisition);
    Rnr rnr = rnrMapper.getRequisitionByFacilityAndProgram(facility.getId(), HIV);
    assertThat(rnr.getId(), is(requisition.getId()));
  }

  @Test
  public void shouldGetRnrById() throws Exception {
    rnrMapper.insert(requisition);

    Rnr returnedRequisition = rnrMapper.getById(requisition.getId());

    assertThat(returnedRequisition.getFacilityId(), CoreMatchers.is(requisition.getFacilityId()));
    assertThat(returnedRequisition.getStatus(), CoreMatchers.is(requisition.getStatus()));
    assertThat(returnedRequisition.getId(), CoreMatchers.is(requisition.getId()));
  }
}
