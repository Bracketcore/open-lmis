package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
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
    private RnrMapper rnrMapper;
    @Autowired
    private RnrLineItemMapper rnrLineItemMapper;

    @Autowired
    private ProgramMapper programMapper;

    Product product;
    Program program;
    ProgramProduct programProduct;
    Integer facilityId;

    @Before
    public void setUp() {
        product = make(a(ProductBuilder.defaultProduct));
        program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        programProduct = new ProgramProduct(program, product, 30);
        facilityId = facilityMapper.insert(make(a(defaultFacility)));
        productMapper.insert(product);
        programProductMapper.insert(programProduct);
    }

    @Test
    public void shouldInsertRequisitionLineItem() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, program.getCode(), RnrStatus.INITIATED, "user"));
        Integer requisitionLineItemId = rnrLineItemMapper.insert(new RnrLineItem(rnrId, programProduct, "user"));
        assertNotNull(requisitionLineItemId);
    }

    @Test
    public void shouldReturnRnrLineItemsByRnrId() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, programProduct, "user");
        rnrLineItemMapper.insert(lineItem);

        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);
        assertThat(rnrLineItems.size(), is(1));
        RnrLineItem rnrLineItem = rnrLineItems.get(0);
        assertThat(rnrLineItem.getRnrId(), is(rnrId));
        assertThat(rnrLineItem.getDosesPerMonth(), is(30));
        assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
        assertThat(rnrLineItem.getProduct(), is("Primary Name Tablet strength mg"));
    }

    @Test
    public void shouldUpdateRnrLineItem() {
        Integer rnrId = rnrMapper.insert(new Rnr(facilityId, "HIV", RnrStatus.INITIATED, "user"));
        RnrLineItem lineItem = new RnrLineItem(rnrId, programProduct, "user");
        Integer generatedId = rnrLineItemMapper.insert(lineItem);
        lineItem.setId(generatedId);
        lineItem.setModifiedBy("user1");
        lineItem.setBeginningBalance(43);
        lineItem.setLossesAndAdjustments(10);
        rnrLineItemMapper.update(lineItem);
        List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnrId);

        assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
        assertThat(rnrLineItems.get(0).getLossesAndAdjustments(), is(10));
        assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
    }
}
