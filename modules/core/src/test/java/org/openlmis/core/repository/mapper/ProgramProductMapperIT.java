package org.openlmis.core.repository.mapper;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramProductMapperIT {
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  ProgramProductMapper programProductMapper;
  private Product product;
  private Program program;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void shouldInsertProductForAProgram() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    assertEquals(1, programProductMapper.insert(programProduct).intValue());
    assertNotNull(programProduct.getId());
  }

  @Test
  public void shouldGetProgramProductByProgramIdAndProductid() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    Integer id = programProductMapper.getIdByProgramAndProductId(program.getId(), product.getId());

    assertThat(id, is(programProduct.getId()));
  }
}
