package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ProgramProductRepositoryTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    ProgramProductRepository programProductRepository;

    @Mock
    ProgramProductMapper programProductMapper;

    @Mock
    private ProgramMapper programMapper;

    @Mock
    private ProductMapper productMapper;

    @Before
    public void setUp() throws Exception {
        programProductRepository = new ProgramProductRepository(programProductMapper, programMapper, productMapper);
    }

    @Test
    public void shouldThrowErrorIfInsertingDuplicateProductForAProgram() throws Exception {
        Product product = make(a(defaultProduct));
        Program program = make(a(ProgramBuilder.defaultProgram));
        ProgramProduct programProduct = new ProgramProduct(program, product);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Product Code " + program.getCode() + " found for Program " + program.getCode());
        doThrow(new DuplicateKeyException("Duplicate Product Code " + program.getCode() + " found for Program " + program.getCode())).when(programProductMapper).insert(programProduct);
        programProductRepository.insert(programProduct);

    }

    @Test
    public void shouldInsertProgramForAProduct() {
        Program program = new Program();
        program.setCode("DummyProgram");
        Product product = new Product();
        product.setCode("DummyProduct");
        ProgramProduct programProduct = new ProgramProduct(program, product);

        programProductRepository.insert(programProduct);
        verify(programProductMapper).insert(programProduct);
    }

    @Test
    public void shouldThrowErrorWhenInsertingProductForInvalidProgram() {
        Product product = make(a(defaultProduct));
        ProgramProduct programProduct = new ProgramProduct(new Program(), product);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid Program Code");
        programProductRepository.insert(programProduct);
    }

    @Test
    public void shouldThrowErrorWhenInsertingInvalidProductForAProgram() {
        Program program = make(a(ProgramBuilder.defaultProgram));
        when(programMapper.getIdByCode(program.getCode())).thenReturn(1);
        ProgramProduct programProduct = new ProgramProduct(program, new Product());

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid Product Code");

        programProductRepository.insert(programProduct);
    }

}
