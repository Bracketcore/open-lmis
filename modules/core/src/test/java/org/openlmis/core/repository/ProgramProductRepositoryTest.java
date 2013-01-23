package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramProductBuilder.PRODUCT_CODE;
import static org.openlmis.core.builder.ProgramProductBuilder.PROGRAM_CODE;
import static org.openlmis.core.repository.ProgramProductRepository.PROGRAM_PRODUCT_INVALID;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ProgramProductRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private ProgramProductRepository programProductRepository;

  @Mock
  ProgramProductMapper programProductMapper;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private ProductMapper productMapper;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private ProgramProductPriceMapper programProductPriceMapper;

  @Before
  public void setUp() throws Exception {
    programProductRepository = new ProgramProductRepository(programRepository, programProductMapper, productRepository, programProductPriceMapper);
  }

  @Test
  public void shouldThrowErrorIfInsertingDuplicateProductForAProgram() throws Exception {
    Product product = make(a(defaultProduct));
    Program program = make(a(defaultProgram));
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate entry for Product Code and Program Code combination found");
    doThrow(new DuplicateKeyException("Duplicate entry for Product Code and Program Code combination found")).when(programProductMapper).insert(programProduct);
    programProductRepository.insert(programProduct);

  }

  @Test
  public void shouldInsertProgramForAProduct() {
    Program program = new Program();
    program.setCode("DummyProgram");
    Product product = new Product();
    product.setCode("DummyProduct");
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);

    programProductRepository.insert(programProduct);
    verify(programProductMapper).insert(programProduct);
  }

  @Test
  public void shouldThrowErrorWhenInsertingProductForInvalidProgram() {
    Product product = make(a(defaultProduct));
    Program program = make(a(defaultProgram));
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    when(programRepository.getIdByCode(programProduct.getProgram().getCode())).thenThrow(new DataException("exception"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("exception");
    programProductRepository.insert(programProduct);
  }

  @Test
  public void shouldThrowErrorWhenInsertingInvalidProductForAProgram() {
    Program program = make(a(defaultProgram));
    when(programRepository.getIdByCode(program.getCode())).thenReturn(1);
    ProgramProduct programProduct = new ProgramProduct(program, new Product(), 10, true);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Product Code");

    programProductRepository.insert(programProduct);
  }

  @Test
  public void shouldGetProgramProductIdByProgramAndProductId() {
    when(programProductMapper.getIdByProgramAndProductId(1, 2)).thenReturn(3);

    assertThat(programProductRepository.getIdByProgramIdAndProductId(1, 2), is(3));
    verify(programProductMapper).getIdByProgramAndProductId(1, 2);
  }

  @Test
  public void shouldThrowExceptionIfNoProgramProductExistForGivenProgramIdAndProductId() {
    when(programProductMapper.getIdByProgramAndProductId(1, 2)).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(PROGRAM_PRODUCT_INVALID);
    programProductRepository.getIdByProgramIdAndProductId(1, 2);
  }


  @Test
  public void shouldGetProgramProductByProgramAndProductCodes() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

    final int programId = 123;
    when(programRepository.getIdByCode(PROGRAM_CODE)).thenReturn(programId);
    final int productId = 12;
    when(productRepository.getIdByCode(PRODUCT_CODE)).thenReturn(productId);
    ProgramProduct expectedProgramProduct = new ProgramProduct();
    when(programProductMapper.getByProgramAndProductId(programId, productId)).thenReturn(expectedProgramProduct);

    ProgramProduct result = programProductRepository.getProgramProductByProgramAndProductCode(programProduct);
    verify(programRepository).getIdByCode(PROGRAM_CODE);
    verify(productRepository).getIdByCode(PRODUCT_CODE);
    verify(programProductMapper).getByProgramAndProductId(programId, productId);

    assertThat(result, is(expectedProgramProduct));
  }

  @Test
  public void shouldThrowExceptionIfProgramProductByProgramAndProductCodesNotFound() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

    final int programId = 123;
    when(programRepository.getIdByCode(PROGRAM_CODE)).thenReturn(programId);
    final int productId = 12;
    when(productRepository.getIdByCode(PRODUCT_CODE)).thenReturn(productId);
    when(programProductMapper.getByProgramAndProductId(programId, productId)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(PROGRAM_PRODUCT_INVALID);
    programProductRepository.getProgramProductByProgramAndProductCode(programProduct);
    verify(programRepository).getIdByCode(PROGRAM_CODE);
    verify(productRepository).getIdByCode(PRODUCT_CODE);
    verify(programProductMapper).getByProgramAndProductId(programId, productId);

  }

  @Test
  public void shouldUpdateCurrentPriceOfProgramProduct() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProductRepository.updateCurrentPrice(programProduct);

    verify(programProductMapper).updateCurrentPrice(programProduct);
  }

  @Test
  public void shouldInsertNewCostWithStartDateAsCurrentAndCloseLastPeriodsCostWithEndDateAsCurrent() throws Exception {
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    programProductRepository.updatePriceHistory(programProductPrice);

    verify(programProductPriceMapper).closeLastActivePrice(programProductPrice);
    verify(programProductPriceMapper).insertNewCurrentPrice(programProductPrice);
  }
}
