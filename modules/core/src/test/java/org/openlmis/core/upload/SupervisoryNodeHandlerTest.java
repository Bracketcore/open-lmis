package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeHandlerTest {

  public static final Integer USER = 1;
  @Mock
  SupervisoryNodeService supervisoryNodeService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode();

    new SupervisoryNodeHandler(supervisoryNodeService).save(supervisoryNode, new AuditFields(USER, null));
    assertThat(supervisoryNode.getModifiedBy(), is(USER));
    assertThat(supervisoryNode.getModifiedDate(), is(notNullValue()));

    verify(supervisoryNodeService).save(supervisoryNode);
  }


}
