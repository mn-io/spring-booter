package net.mnio.springbooter.services.user;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
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
        final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto("email", "", "password");
        final User expectedUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        final User actualUser = userService.createUser(dto);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(dto.getEmail(), captor.getValue().getEmail());

        assertEquals(actualUser, expectedUser);
    }

}