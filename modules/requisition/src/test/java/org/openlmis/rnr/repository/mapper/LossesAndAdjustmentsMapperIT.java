package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.rnr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class LossesAndAdjustmentsMapperIT {


    public static final Integer HIV = 1;
    @Autowired
    LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    @Autowired
    RnrMapper rnrMapper;

    @Autowired
    RnrLineItemMapper rnrLineItemMapper;
    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramMapper programMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProgramProductMapper programProductMapper;

    RnrLineItem rnrLineItem;
    LossesAndAdjustments lossesAndAdjustments;

    @Before
    public void setUp() throws Exception {
        Product product = make(a(ProductBuilder.defaultProduct));
        Program program = make(a(ProgramBuilder.defaultProgram));
        programMapper.insert(program);
        ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, 12.5F);
        productMapper.insert(product);
        programProductMapper.insert(programProduct);
        FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct("warehouse", programProduct, 3);
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityMapper.insert(facility);

        Rnr requisition = new Rnr(facility.getId(), HIV, RnrStatus.INITIATED, "user");
        rnrMapper.insert(requisition);

        rnrLineItem = new RnrLineItem(requisition.getId(), facilityApprovedProduct, "user");
        rnrLineItemMapper.insert(rnrLineItem);
        lossesAndAdjustments = new LossesAndAdjustments();
        lossesAndAdjustments.setType(LossesAndAdjustmentType.CLINIC_RETURN);
        lossesAndAdjustments.setQuantity(20);
    }

    @Test
    public void shouldInsertLossesAndAdjustments() {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        assertThat(id, is(notNullValue()));
    }

    @Test
    public void shouldGetLossesAndAdjustmentByRequisitionLineItemId() {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        List<LossesAndAdjustments> lossesAndAdjustments = lossesAndAdjustmentsMapper.getByRequisitionLineItem(rnrLineItem);
        assertThat(lossesAndAdjustments.size(), is(1));
        assertThat(lossesAndAdjustments.get(0).getId(), is(id));
        assertThat(lossesAndAdjustments.get(0).getQuantity(), is(20));
    }

}
