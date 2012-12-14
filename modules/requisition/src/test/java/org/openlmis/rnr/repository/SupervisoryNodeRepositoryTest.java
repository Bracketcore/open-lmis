package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.openlmis.rnr.repository.mapper.SupervisoryNodeMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeRepositoryTest {

    SupervisoryNode supervisoryNode;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    SupervisoryNodeMapper supervisoryNodeMapper;

    @Mock
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        supervisoryNode = new SupervisoryNode();
        SupervisoryNode parent = new SupervisoryNode();
        parent.setCode("PSN");
        supervisoryNode.setParent(parent);
        supervisoryNode.setFacility(new Facility());
    }

    @Test
    public void shouldGiveErrorIfDuplicateCodeFound() throws Exception {
        doThrow(new DuplicateKeyException("")).when(supervisoryNodeMapper).insert(supervisoryNode);
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Supervisory Node Code");

        new SupervisoryNodeRepository(supervisoryNodeMapper, facilityMapper).save(supervisoryNode);

        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }

    @Test
    public void shouldGiveErrorIfParentNodeCodeDoesNotExist() throws Exception {

        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(null);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Supervisory Node as Parent does not exist");

        new SupervisoryNodeRepository(supervisoryNodeMapper, facilityMapper).save(supervisoryNode);

        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
    }

    @Test
    public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);
        when(facilityMapper.getIdForCode(supervisoryNode.getFacility().getCode())).thenReturn(null);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Facility Code does not exist");

        new SupervisoryNodeRepository(supervisoryNodeMapper, facilityMapper).save(supervisoryNode);

        verify(facilityMapper).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
    }

    @Test
    public void shouldSaveSupervisoryNode() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);
        when(facilityMapper.getIdForCode(supervisoryNode.getFacility().getCode())).thenReturn(1);

        new SupervisoryNodeRepository(supervisoryNodeMapper, facilityMapper).save(supervisoryNode);

        verify(facilityMapper).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
        assertThat(supervisoryNode.getParent().getId(), is(1));
        assertThat(supervisoryNode.getFacility().getId(), is(1));
        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }

    @Test
    public void shouldSaveSupervisoryNodeIfParentNotSupplied() throws Exception {
        when(facilityMapper.getIdForCode(supervisoryNode.getFacility().getCode())).thenReturn(1);
        supervisoryNode.setParent(null);
        new SupervisoryNodeRepository(supervisoryNodeMapper, facilityMapper).save(supervisoryNode);

        verify(facilityMapper).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper, never()).getIdForCode(anyString());
        assertThat(supervisoryNode.getParent(), is(nullValue()));
        assertThat(supervisoryNode.getFacility().getId(), is(1));
        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }
}
