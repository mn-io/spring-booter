package net.mnio.springbooter.controller;

import net.mnio.springbooter.AbstractIntegrationTest;
import net.mnio.springbooter.bootstrap.filter.AuthFilter;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.controller.api.UserLoginDto;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Testing business use cases end to end.
 */
public class UserUseCaseIntegrationTest extends AbstractIntegrationTest {

    private final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private final String URL_ROOT_USERS = "/users";

    private final String URL_ROOT_USER_ME = "/users/me";

    private final String URL_ROOT_SESSION = "/session";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Test
    public void createNewUserForLoginWithUpdateAndLogoutHappyPath() throws Exception {
        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());
        final String sessionToken = doAndVerifyLoginSession(loginDto);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore + 1, userSessionRepository.count());

        doAndVerifyGetUser(sessionToken, createDto);
        final UserCreateOrUpdateDto updateDto = new UserCreateOrUpdateDto("test2@test.com", "New Name", "new password");
        doAndVerifyUpdateUser(sessionToken, updateDto);
        doAndVerifyGetUser(sessionToken, updateDto);
        doAndVerifyLogoutSession(sessionToken);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void deleteUserWithOpenSessions() throws Exception {
        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());

        final String sessionToken = doAndVerifyLoginSession(loginDto);
        doAndVerifyLoginSession(loginDto);
        doAndVerifyLoginSession(loginDto);

        assertEquals(userCountBefore + 1, userRepository.count());

        doAndVerifyDeleteUser(sessionToken);

        assertEquals(userCountBefore, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void sessionsReturnWithDifferentIds() throws Exception {
        final long sessionCountBefore = userSessionRepository.count();

        final UserCreateOrUpdateDto createDto = new UserCreateOrUpdateDto("test1@test.com", "TestiMcTestFace", "password");

        doAndVerifySignUp(createDto);

        final UserLoginDto loginDto = new UserLoginDto(createDto.getEmail(), createDto.getPassword());

        final int maxSessions = 5;
        final Set<String> tokens = new HashSet<>(maxSessions);
        for (int i = 0; i < maxSessions; i++) {
            final String sessionToken = doAndVerifyLoginSession(loginDto);
            assertFalse(tokens.contains(sessionToken));
            tokens.add(sessionToken);
        }

        assertEquals(sessionCountBefore + maxSessions, userSessionRepository.count());
    }

    private void doAndVerifySignUp(final UserCreateOrUpdateDto createDto) throws Exception {
        final String createPostBody = mapper.writeValueAsString(createDto);
        final MockHttpServletRequestBuilder signUpRequest = post(URL_ROOT_USERS)
                .content(createPostBody)
                .contentType(CONTENT_TYPE_JSON);
        mvc.perform(signUpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createDto.getName()))
                .andExpect(jsonPath("$.email").value(createDto.getEmail()));
    }

    private String doAndVerifyLoginSession(final UserLoginDto loginDto) throws Exception {
        final String loginPostBody = mapper.writeValueAsString(loginDto);

        final MockHttpServletRequestBuilder loginRequest = post(URL_ROOT_SESSION)
                .content(loginPostBody)
                .contentType(CONTENT_TYPE_JSON);
        final MvcResult loginResponse = mvc.perform(loginRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(loginDto.getEmail()))
                .andReturn();

        final String sessionToken = loginResponse.getResponse()
                .getHeader(AuthFilter.HEADER_NAME_SESSION_TOKEN);
        assertTrue(StringUtils.isNotBlank(sessionToken));
        return sessionToken;
    }

    private void doAndVerifyGetUser(final String sessionToken, final UserCreateOrUpdateDto createDto) throws
            Exception {
        final MockHttpServletRequestBuilder meRequest = get(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(meRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createDto.getName()))
                .andExpect(jsonPath("$.email").value(createDto.getEmail()));
    }

    private void doAndVerifyUpdateUser(final String sessionToken, final UserCreateOrUpdateDto updateDto) throws
            Exception {
        final String updatePutBody = mapper.writeValueAsString(updateDto);

        final MockHttpServletRequestBuilder meUpdateRequest = put(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken)
                .content(updatePutBody)
                .contentType(CONTENT_TYPE_JSON);
        mvc.perform(meUpdateRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }

    private void doAndVerifyLogoutSession(final String sessionToken) throws Exception {
        final MockHttpServletRequestBuilder logoutRequest = delete(URL_ROOT_SESSION)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void doAndVerifyDeleteUser(final String sessionToken) throws Exception {
        final MockHttpServletRequestBuilder deleteRequest = delete(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(deleteRequest)
                .andDo(print())
                .andExpect(status().isOk());
    }
}