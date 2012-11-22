package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;
import org.openlmis.rnr.repository.mapper.RnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class ProgramRnrColumnMapperIT {

    public static final String HIV = "HIV";
    @Autowired
    ProgramRnrColumnMapper programRnrColumnMapper;

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    @Before
    public void setUp() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 5, false, "Some Random Label");

        List<RnrColumn> fetchedColumns = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(fetchedColumns.size(), is(1));
        assertThat(fetchedColumns.get(0).getLabel(), is("Some Random Label"));
        assertThat(fetchedColumns.get(0).isVisible(), is(false));
        assertThat(fetchedColumns.get(0).getPosition(), is(5));
    }

    @Test
    public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 3, true, "Some Random Label");
        updateProgramRnrColumn(rnrColumn.getId(), 5, false, "Some Random Label");

        RnrColumn updatedRnrColumn = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV).get(0);

        assertThat(updatedRnrColumn.getId(), is(rnrColumn.getId()));
        assertThat(updatedRnrColumn.isVisible(), is(false));
        assertThat(updatedRnrColumn.getPosition(), is(5));
        assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
    }

    @Test
    public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
        RnrColumn visibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(visibleColumn1, 4, true, "Some Random Label");
        RnrColumn visibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn2, 3, true, "Some Random Label");
        RnrColumn notVisibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(2);
        addProgramRnrColumn(notVisibleColumn1, 2, false, "Some Random Label");
        RnrColumn notVisibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(3);
        addProgramRnrColumn(notVisibleColumn2, 1, false, "Some Random Label");

        List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
        assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
        assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
        assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));
    }

    @Test
    public void shouldRetrieveVisibleProgramRnrColumn() {
        RnrColumn visibleColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        RnrColumn inVisibleColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn, 1, true, "Col1");
        addProgramRnrColumn(inVisibleColumn, 2, false, "Col2");

        List<RnrColumn> rnrColumns = programRnrColumnMapper.getVisibleProgramRnrColumns(HIV);
        assertThat(rnrColumns.size(), is(1));
        assertThat(rnrColumns.get(0).isVisible(), is(true));
    }


    private int addProgramRnrColumn(RnrColumn rnrColumn, int position, boolean visible, String label) {
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        return programRnrColumnMapper.insert(HIV, rnrColumn);
    }

    private void updateProgramRnrColumn(Integer id, int position, boolean visible, String label) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setId(id);
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        programRnrColumnMapper.update(HIV, rnrColumn);
    }

}
