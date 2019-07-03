package net.mnio.springbooter.services.user;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.security.PasswordUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceUnitTest extends AbstractUnitTest {

    @Autowired
    UserSessionRepository userSessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    public void createUserHappyPath() {
        final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto("email", "name", "password");
        final User expectedUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        final User actualUser = userService.createUser(dto);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(dto.getEmail(), captor.getValue().getEmail());
        assertEquals(dto.getName(), captor.getValue().getName());
        assertTrue(PasswordUtil.checkPassword(dto.getPassword(), captor.getValue().getPassword()));

        assertEquals(actualUser, expectedUser);
    }

    @Test
    public void createUserWithFixedEmail() {
        final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto(" EMail ", "", "password");

        userService.createUser(dto);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(dto.getEmail().toLowerCase().trim(), captor.getValue().getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void dontCreateUserWithEmptyEmail() {
        final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto("", "", "password");
        userService.createUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dontCreateUserWithEmptyPassword() {
        final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto("email", "", "");
        userService.createUser(dto);
    }

    @Test
    public void checkPasswordCorrect() {
        final User user = new User();
        final String plaintextPassword = "password";
        user.setPassword(PasswordUtil.hashPasswordWithSalt(plaintextPassword));

        assertTrue(userService.checkPassword(user, plaintextPassword));
        assertFalse(userService.checkPassword(user, "not correct"));
        assertFalse(userService.checkPassword(user, ""));
        assertFalse(userService.checkPassword(user, null));
    }
}