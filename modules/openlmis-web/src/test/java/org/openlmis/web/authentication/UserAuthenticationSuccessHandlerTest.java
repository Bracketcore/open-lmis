package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.authentication.UserAuthenticationSuccessHandler.IS_ADMIN;
import static org.openlmis.web.authentication.UserAuthenticationSuccessHandler.USER;

public class UserAuthenticationSuccessHandlerTest {

    public static final String CONTEXT_PATH = "contextPath";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Mock
    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Mock
    private HttpSession session;

    String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Before
    public void setup() {
        initMocks(this);
        userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();

        when(request.getSession()).thenReturn(session);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldRedirectUserToHomeWhenNoSavedRequestPresent() throws IOException, ServletException {
        String defaultTargetUrl = "/";

        Authentication authentication = new TestingAuthenticationToken(USERNAME, "password", "USER");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals(CONTEXT_PATH + defaultTargetUrl, response.getRedirectedUrl());
    }

    @Test
    public void shouldRedirectToRequestedUrl() throws ServletException, IOException {
        String previousUrl = "/previousUrl";

        DefaultSavedRequest previousRequest = mock(DefaultSavedRequest.class);
        when(previousRequest.getRedirectUrl()).thenReturn(previousUrl);
        when(session.getAttribute(SAVED_REQUEST)).thenReturn(previousRequest);

        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, new UsernamePasswordAuthenticationToken(USERNAME, "password"));
        assertEquals(CONTEXT_PATH + previousUrl, response.getRedirectedUrl());
    }


    @Test
    public void shouldSaveUsernameInSession() throws IOException, ServletException {
        Authentication authentication = new TestingAuthenticationToken(USERNAME, "password", "USER");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
    }

    @Test

    public void shouldSaveUserIfAdminInSession() throws IOException, ServletException {
        Authentication authentication = new TestingAuthenticationToken(USERNAME, PASSWORD, "ADMIN");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
        verify(session).setAttribute(IS_ADMIN, true);
    }

}
