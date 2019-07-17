package net.mnio.springbooter.controller;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl;
import net.mnio.jConcurrencyOrchestra.test.Task;
import net.mnio.springbooter.AbstractConcurrencyTest;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.controller.api.UserLoginDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerConcurrencyTest extends AbstractConcurrencyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterruptService interruptService;

    @Test
    public void updateUserTwice() throws Exception {
        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "crated", "password");
        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());
        final UserCreateOrUpdateDto updateDto1 = new UserCreateOrUpdateDto(createDto.getEmail(), "update1", createDto.getPassword());
        final UserCreateOrUpdateDto updateDto2 = new UserCreateOrUpdateDto(createDto.getEmail(), "update2", createDto.getPassword());

        final UserControllerUtilWrapper util = new UserControllerUtilWrapper(mvc, mapper);

        final Task task0SetupUser = () -> {
            util.doAndVerifySignUp(createDto);
        };

        final Task task1ToBeInterrupted = () -> {
            final String sessionToken = util.doAndVerifyLoginSession(loginDto);
            util.doUpdateUser(sessionToken, updateDto1)
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.name").value(ObjectOptimisticLockingFailureException.class.getSimpleName()));
        };

        final Task task2CreatingUserSuccessfully = () -> {
            final String sessionToken = util.doAndVerifyLoginSession(loginDto);
            util.doAndVerifyUpdateUser(sessionToken, updateDto2);
        };

        final boolean success = ((OrchestratedInterruptServiceImpl) interruptService)
                .start(
                        task0SetupUser, // interrupted within UserService
                        task0SetupUser, // but it continues (this task will run until end now)
                        task1ToBeInterrupted, // interrupted within UserService. but no continue...
                        task2CreatingUserSuccessfully, // interrupted within UserService
                        task2CreatingUserSuccessfully // but it continues (this task will run until end now)
                );

        assertTrue(success);

        final User user = userRepository.findByEmail(createDto.getEmail()).get();
        assertEquals(user.getName(), updateDto2.getName());
        assertEquals(user.getVersion(), 1);
    }
}