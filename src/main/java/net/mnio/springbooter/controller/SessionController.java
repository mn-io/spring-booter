package net.mnio.springbooter.controller;

import net.mnio.springbooter.bootstrap.filter.AuthFilter;
import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.bootstrap.session.UserSessionContext;
import net.mnio.springbooter.controller.api.UserDto;
import net.mnio.springbooter.controller.api.UserLoginDto;
import net.mnio.springbooter.controller.error.exceptions.UserForbiddenHttpException;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.services.user.UserService;
import net.mnio.springbooter.services.user.UserSessionService;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Log
    private Logger log;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService userSessionService;

    /*
     * User verification is not part of any service as HttpExceptions are thrown
     * which should be managed by controller logic.
     */
    @PermitPublic
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> login(@RequestBody final UserLoginDto dto) {
        final String email = dto.getEmail();
        final Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            log.debug("User {} not found", email);
            throw new UserForbiddenHttpException();
        }

        final String password = dto.getPassword();
        final User userFound = user.get();
        final boolean pwdOk = userService.checkPassword(userFound, password);
        if (!pwdOk) {
            log.debug("User {} with wrong password", email);
            throw new UserForbiddenHttpException();
        }

        final UserSession session = userSessionService.createSession(userFound);

        return ResponseEntity.ok()
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, session.getToken())
                .body(UserDto.build(userFound));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void logout() {
        final UserSession session = UserSessionContext.getSession();
        userSessionService.destroySession(session);
    }
}
