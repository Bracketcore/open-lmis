package org.openlmis.web.authentication;

import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.authentication.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

public class UserAuthenticationProvider implements AuthenticationProvider {

    private UserAuthenticationService userAuthenticationService;


    @Autowired
    public UserAuthenticationProvider(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userName = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        UserToken userToken = userAuthenticationService.authorizeUser(userName, password);

        if(!userToken.isAuthenticated()){
            return new UsernamePasswordAuthenticationToken(userName, password);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new GrantedAuthority[]{getGrantedAuthority(userToken.getUser().getRole())});

        return new UsernamePasswordAuthenticationToken(userName, password, authorities);
    }

    private GrantedAuthority getGrantedAuthority(final String role) {
        return new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        };
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
