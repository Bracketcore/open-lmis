package org.openlmis.restapi.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.CHW;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.CHWBuilder.defaultCHW;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest(RestCHWService.class)
public class RestCHWServiceTest {

  @Mock
  FacilityService facilityService;

  @InjectMocks
  RestCHWService restCHWService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldCreateFacilityForCHW() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(chw);

    Facility facility = mock(Facility.class);
    when(facilityService.getFacilityWithReferenceDataForCode(chw.getParentFacilityCode())).thenReturn(baseFacility);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    restCHWService.create(chw);

    verify(facility, times(2)).setCode(chw.getAgentCode());
    verify(facility).setParentFacilityId(baseFacility.getId());
    verify(facility).setName(chw.getAgentName());
    verify(facility).setFacilityType(baseFacility.getFacilityType());
    verify(facility).setMainPhone(chw.getPhoneNumber());
    verify(facility).setGeographicZone(baseFacility.getGeographicZone());
    verify(facility).setActive(Boolean.parseBoolean(chw.getActive()));
    verify(facility).setVirtualFacility(true);
    verify(facility).setSdp(true);
    verify(facility).setDataReportable(true);
    verify(facility).setOperatedBy(baseFacility.getOperatedBy());
    verify(facility).setGoLiveDate(currentTimeStamp);
    verify(facilityService).save(facility);
  }

  @Test
  public void shouldUpdateACHWFacility() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(chw);

    when(facilityService.getFacilityWithReferenceDataForCode(chw.getParentFacilityCode())).thenReturn(baseFacility);
    Date currentTimeStamp = mock(Date.class);
    whenNew(Date.class).withNoArguments().thenReturn(currentTimeStamp);

    Facility chwFacility = spy(new Facility());
    chwFacility.setVirtualFacility(true);
    whenNew(Facility.class).withNoArguments().thenReturn(chwFacility);
    when(facilityService.getByCode(chwFacility)).thenReturn(chwFacility);

    restCHWService.update(chw);

    verify(chwFacility).setName(chw.getAgentName());
    verify(chwFacility).setMainPhone(chw.getPhoneNumber());
    verify(chwFacility).setActive(Boolean.parseBoolean(chw.getActive()));
    verify(chwFacility).setParentFacilityId(baseFacility.getId());
    verify(chwFacility).setGeographicZone(baseFacility.getGeographicZone());
    verify(chwFacility).setFacilityType(baseFacility.getFacilityType());
    verify(chwFacility).setOperatedBy(baseFacility.getOperatedBy());
    verify(facilityService).update(chwFacility);
  }

  @Test
  public void shouldThrowExceptionIfAgentCodeIsMissing() throws Exception {

    CHW chw = make(a(defaultCHW));
    chw.setAgentCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restCHWService.create(chw);
  }

  @Test
  public void shouldThrowExceptionIfAgentNameIsMissing() throws Exception {

    CHW chw = make(a(defaultCHW));
    chw.setAgentName(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restCHWService.create(chw);
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityCodeIsMissing() throws Exception {

    CHW chw = make(a(defaultCHW));
    chw.setParentFacilityCode(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restCHWService.create(chw);
  }

  @Test
  public void shouldThrowExceptionIfBaseFacilityIsVirtualFacility() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(chw);
    baseFacility.setVirtualFacility(true);
    when(facilityService.getFacilityWithReferenceDataForCode(chw.getParentFacilityCode())).thenReturn(baseFacility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.parent.facility.virtual");

    restCHWService.create(chw);
  }

  @Test
  public void shouldThrowExceptionIfCHWIsAlreadyRegistered() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);
    when(facilityService.getByCode(facility)).thenReturn(facility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.chw.already.registered");

    restCHWService.create(chw);
  }

  @Test
  public void shouldThrowExceptionIfActiveFieldIsNullOnUpdate() throws Exception {
    CHW chw = make(a(defaultCHW));
    chw.setActive(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    restCHWService.update(chw);
  }

  @Test
  public void shouldThrowExceptionIfCHWIsNotVirtualOnUpdate() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility nonVirtualFacility = new Facility();
    nonVirtualFacility.setVirtualFacility(false);
    nonVirtualFacility.setCode(chw.getAgentCode());
    when(facilityService.getByCode(nonVirtualFacility)).thenReturn(nonVirtualFacility);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.chw.not.virtual");

    restCHWService.update(chw);
  }

  @Test
  public void shouldThrowExceptionIfInvalidAgentCodeOnUpdate() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility facility = mock(Facility.class);
    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.agent.code");

    restCHWService.update(chw);
  }

  private Facility getBaseFacility(CHW chw) {
    Facility baseFacility = new Facility(1l);
    baseFacility.setCode(chw.getParentFacilityCode());
    baseFacility.setFacilityType(new FacilityType());
    baseFacility.setGeographicZone(new GeographicZone());
    baseFacility.setOperatedBy(new FacilityOperator());
    return baseFacility;
  }

}
