package org.openlmis.core.repository.mapper;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramMapperIT extends SpringIntegrationTest {

    public static final String PROGRAM_CODE = "HIV";

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramMapper programMapper;

    @Before
    @After
    public void setUp() {
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();
    }

    @Test
    public void shouldGetAllActiveProgram() {
        List<Program> programs = programMapper.getAllActive();
        assertEquals(6, programs.size());
        assertTrue("program does not exists", programs.contains(new Program("HIV", "HIV", "HIV", true)));
    }

    @Test
    public void shouldGetProgramsWhichAreActiveByFacilityId() {
        Facility facility = make(a(FacilityBuilder.facility));
        facilityMapper.insert(facility);
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(), PROGRAM_CODE, true, "test", DateTime.now().toDate()));

        List<Program> programs = programMapper.getActiveByFacilityCode("F10010");

        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getCode(), is(PROGRAM_CODE));
    }
}
