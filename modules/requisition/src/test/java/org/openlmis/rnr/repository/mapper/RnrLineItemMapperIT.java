package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;

@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class RnrLineItemMapperIT {

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProgramProductMapper programProductMapper;
    @Autowired
    private FacilityApprovedProductMapper facilityApprovedProductMapper;
    @Autowired
    private RnrMapper rnrMapper;
    @Autowired
    private RnrLineItemMapper rnrLineItemMapper;

    @Autowired
    private ProgramMapper programMapper;

    FacilityApprovedProduct facilityApprovedProduct;
    Integer facilityId;

    @Before
    public void setUp() {
        Product product = make(a(ProductBuilder.defaultProduct));
        Program program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, 12.5F);
        facilityId = facilityMapper.insert(make(a(defaultFacility)));
        productMapper.insert(product);
        programProductMapper.insert(programProduct);
        facilityApprovedProduct = new FacilityApprovedProduct("warehouse", programProduct, 3);
        facilityApprovedProductMapper.insert(facilityApprovedProduct);
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, PROGRAM_CODE, RnrStatus.INITIATED, "user"));
		Integer requisitionLineItemId = rnrLineItemMapper.insert(new RnrLineItem(rnrId, facilityApprovedProduct, "user"));
        assertNotNull(requisitionLineItemId);
    }

    @Test
    public void shouldReturnRnrLineItemsByRnrId() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, facilityApprovedProduct, "user");
		lineItem.setPacksToShip(20);
        rnrLineItemMapper.insert(lineItem);

        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);
        assertThat(rnrLineItems.size(), is(1));
        RnrLineItem rnrLineItem = rnrLineItems.get(0);
        assertThat(rnrLineItem.getRnrId(), is(rnrId));
        assertThat(rnrLineItem.getDosesPerMonth(), is(30));
        assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
        assertThat(rnrLineItem.getProduct(), is("Primary Name Tablet strength mg"));
		assertThat(rnrLineItem.getPacksToShip(), is(20));
		assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
		assertThat(rnrLineItem.getRoundToZero(), is(true));
		assertThat(rnrLineItem.getPackSize(), is(10));
		assertThat(rnrLineItem.getPrice(), is(12.5F));
    }

    @Test
    public void shouldUpdateRnrLineItem() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, facilityApprovedProduct, "user");
        Integer generatedId = rnrLineItemMapper.insert(lineItem);
        lineItem.setId(generatedId);
        lineItem.setModifiedBy("user1");
        lineItem.setBeginningBalance(43);
        lineItem.setLossesAndAdjustments(10);
        int updateCount = rnrLineItemMapper.update(lineItem);
		assertThat(updateCount, is(1));
        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);

        assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
        assertThat(rnrLineItems.get(0).getLossesAndAdjustments(), is(10));
        assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
    }
}
