package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.RegimenBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class RegimenMapperIT {

  @Autowired
  RegimenMapper mapper;

  Regimen regimen;

  @Before
  public void setUp() throws Exception {
    regimen = make(a(defaultRegimen));
  }

  @Test
  public void shouldInsertARegimen() throws Exception {

    mapper.insert(regimen);

    assertNotNull(regimen.getId());
  }

  @Test
  public void shouldGetAllRegimenForAProgramOrderByCategoryAndDisplayOrder(){
    Long progId = 1l;
    Regimen adultRegimen1 = make(a(defaultRegimen, with(regimenCode,"CODE_1"), with(displayOrder, 1), with(programId, progId)));
    mapper.insert(adultRegimen1);
    Regimen adultRegimen2 = make(a(defaultRegimen, with(regimenCode,"CODE_2"), with(displayOrder, 2), with(programId, progId)));
    mapper.insert(adultRegimen2);
    RegimenCategory regimenCategory = new RegimenCategory("PAEDIATRICS", "Paediatrics", 2);
    regimenCategory.setId(2l);
    Regimen paediatricsRegimen1 = make(a(defaultRegimen, with(regimenCode,"CODE_4"), with(displayOrder, 1), with(category, regimenCategory), with(programId, progId)));
    mapper.insert(paediatricsRegimen1);
    Regimen paediatricsRegimen2 = make(a(defaultRegimen, with(regimenCode,"CODE_3"), with(displayOrder, 2), with(category, regimenCategory), with(programId, progId)));
    mapper.insert(paediatricsRegimen2);


    List<Regimen> regimens = mapper.getByProgram(progId);

    assertThat(regimens.size(), is(4));
    assertThat(regimens.get(0).getCode(), is("CODE_1"));
    assertThat(regimens.get(1).getCode(), is("CODE_2"));
    assertThat(regimens.get(2).getCode(), is("CODE_4"));
    assertThat(regimens.get(3).getCode(), is("CODE_3"));
  }

  @Test
  public void shouldUpdateARegimen(){
    mapper.insert(regimen);
    regimen.setName("Regimen Updated Name");
    regimen.setCode("Regimen Updated Code");
    regimen.setActive(true);
    regimen.setDisplayOrder(2);

    mapper.update(regimen);

    List<Regimen> updatedRegimenList = mapper.getByProgram(regimen.getProgramId());
    assertThat(updatedRegimenList.get(0).getName(),is("Regimen Updated Name"));
    assertThat(updatedRegimenList.get(0).getCode(),is("Regimen Updated Code"));
    assertThat(updatedRegimenList.get(0).getActive(),is(true));
    assertThat(updatedRegimenList.get(0).getDisplayOrder(),is(2));
  }

}
