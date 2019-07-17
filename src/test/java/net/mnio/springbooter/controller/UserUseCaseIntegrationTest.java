package net.mnio.springbooter.controller;

import net.mnio.springbooter.AbstractIntegrationTest;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.controller.api.UserLoginDto;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * Testing business use cases end to end.
 */
public class UserUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Test
    public void createNewUserForLoginWithUpdateAndLogoutHappyPath() throws Exception {
        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        final UserControllerUtilWrapper util = new UserControllerUtilWrapper(mvc, mapper);
        util.doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());
        final String sessionToken = util.doAndVerifyLoginSession(loginDto);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore + 1, userSessionRepository.count());

        util.doAndVerifyGetUser(sessionToken, createDto);
        final UserCreateOrUpdateDto updateDto = new UserCreateOrUpdateDto("test2@test.com", "New Name", "new password");
        util.doAndVerifyUpdateUser(sessionToken, updateDto);
        util.doAndVerifyGetUser(sessionToken, updateDto);
        util.doAndVerifyLogoutSession(sessionToken);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void deleteUserWithOpenSessions() throws Exception {
        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        final UserControllerUtilWrapper util = new UserControllerUtilWrapper(mvc, mapper);
        util.doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());

        final String sessionToken = util.doAndVerifyLoginSession(loginDto);
        util.doAndVerifyLoginSession(loginDto);
        util.doAndVerifyLoginSession(loginDto);

        assertEquals(userCountBefore + 1, userRepository.count());

        util.doAndVerifyDeleteUser(sessionToken);

        assertEquals(userCountBefore, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void sessionsReturnWithDifferentIds() throws Exception {
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        final UserControllerUtilWrapper util = new UserControllerUtilWrapper(mvc, mapper);
        util.doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());

        final int maxSessions = 5;
        final Set<String> tokens = new HashSet<>(maxSessions);
        for (int i = 0; i < maxSessions; i++) {
            final String sessionToken = util.doAndVerifyLoginSession(loginDto);
            assertFalse(tokens.contains(sessionToken));
            tokens.add(sessionToken);
        }

        assertEquals(sessionCountBefore + maxSessions, userSessionRepository.count());
    }
}