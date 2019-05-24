package net.mnio.springbooter.controller;

import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.bootstrap.session.UserSessionContext;
import net.mnio.springbooter.controller.api.UserDto;
import net.mnio.springbooter.controller.api.UserSignupDto;
import net.mnio.springbooter.controller.api.UserUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.services.user.UserService;
import net.mnio.springbooter.services.user.UserSessionService;
import net.mnio.springbooter.services.user.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UserVerificationService userVerificationService;

    @PermitPublic
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> signup(@RequestBody final UserSignupDto userSignupDto) {
        final User user = userVerificationService.createUser(userSignupDto);
        return ResponseEntity.ok(UserDto.build(user));
    }

    @PermitPublic
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<UserDto>> listAll(final Pageable pageable) {
        final Page<User> page = userService.getAll(pageable);
        return ResponseEntity.ok(page.map(UserDto::build));
    }

    @RequestMapping(value = "/me", method = RequestMethod.PUT)
    public ResponseEntity<UserDto> updateMe(@RequestBody final UserUpdateDto userUpdateDto) {
        final User user = UserSessionContext.getSession().getUser();
        final User updated = userService.updateUser(user, userUpdateDto);
        return ResponseEntity.ok(UserDto.build(updated));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getMe() {
        final User user = UserSessionContext.getSession().getUser();
        return ResponseEntity.ok().body(UserDto.build(user));
    }

    @RequestMapping(value = "/me", method = RequestMethod.DELETE)
    public void deleteMe() {
        final User user = UserSessionContext.getSession().getUser();
        userVerificationService.deleteUser(user);
    }
}
