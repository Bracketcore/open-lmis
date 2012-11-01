package org.openlmis.authentication.service;

import org.openlmis.authentication.UserToken;
import org.openlmis.authentication.dao.UserMapper;
import org.openlmis.authentication.domain.User;
import org.openlmis.utils.hash.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {

    private static final boolean AUTHORIZATION_SUCCESSFUL = true;
    private static final boolean AUTHORIZATION_FAILED = false;

    private UserMapper userMapper;

    @Autowired
    public UserAuthenticationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserToken authorizeUser(String userName, String password) {
        String passwordHash = Encoder.hash(password);
        User fetchedUser = userMapper.selectUserByUserNameAndPassword(userName, passwordHash);
        if (fetchedUser == null) return new UserToken(userName, null, AUTHORIZATION_FAILED);

        return new UserToken(fetchedUser.getUserName(), fetchedUser.getRole(), AUTHORIZATION_SUCCESSFUL);
    }
}
