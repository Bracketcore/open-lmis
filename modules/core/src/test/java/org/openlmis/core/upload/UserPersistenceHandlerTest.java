package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserPersistenceHandlerTest {

  private UserPersistenceHandler userPersistenceHandler;

  @Mock
  private UserService userService;

  @Test
  public void shouldSaveAUser() throws Exception {
    userPersistenceHandler = new UserPersistenceHandler(userService);
    User user = new User();
    userPersistenceHandler.save(user, "userName");
    verify(userService).save(eq(user), anyMap());
    assertThat(user.getModifiedBy(), is("userName"));
  }
}
