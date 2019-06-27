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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserLifecycleIntegrationTest extends AbstractIntegrationTest {

    private final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private final String URL_ROOT_USERS = "/users";

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
        final String sessionToken = doAndVerifyLogin(loginDto);

        final long sessionCountInBetween = userSessionRepository.count();
        assertEquals(sessionCountBefore + 1, sessionCountInBetween);


        doAndVerifyGetUser(sessionToken, createDto);
        final UserCreateOrUpdateDto updateDto = new UserCreateOrUpdateDto("test2@test.com", "New Name", "new password");
        doAndVerifyPutUser(sessionToken, updateDto);
        doAndVerifyGetUser(sessionToken, updateDto);
        doAndVerifyLogout(sessionToken);

        final long userCountAfter = userRepository.count();
        assertEquals(userCountBefore + 1, userCountAfter);
        final long sessionCountAfter = userSessionRepository.count();
        assertEquals(sessionCountBefore, sessionCountAfter);
    }

//    @Test
//    public void createNewUserForLoginWithUpdateAndLogoutHappyPath() throws Exception {
//        _
//    }

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

    private String doAndVerifyLogin(final UserLoginDto loginDto) throws Exception {
        final String loginPostBody = mapper.writeValueAsString(loginDto);

        final MockHttpServletRequestBuilder loginRequest = post(URL_ROOT_SESSION)
                .content(loginPostBody)
                .contentType(CONTENT_TYPE_JSON);
        final MvcResult loginResponse = mvc.perform(loginRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(loginDto.getEmail()))
                .andReturn();

        // TODO: test return of different ids
        final String sessionToken = loginResponse.getResponse()
                .getHeader(AuthFilter.HEADER_NAME_SESSION_TOKEN);
        assertTrue(StringUtils.isNotEmpty(sessionToken));
        return sessionToken;
    }

    private void doAndVerifyGetUser(final String sessionToken, final UserCreateOrUpdateDto createDto) throws
            Exception {
        final MockHttpServletRequestBuilder meRequest = get(URL_ROOT_USERS + "/me")
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(meRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createDto.getName()))
                .andExpect(jsonPath("$.email").value(createDto.getEmail()));
    }

    private void doAndVerifyPutUser(final String sessionToken, final UserCreateOrUpdateDto updateDto) throws
            Exception {
        final String updatePutBody = mapper.writeValueAsString(updateDto);

        final MockHttpServletRequestBuilder meUpdateRequest = put(URL_ROOT_USERS + "/me")
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken)
                .content(updatePutBody)
                .contentType(CONTENT_TYPE_JSON);
        mvc.perform(meUpdateRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }

    private void doAndVerifyLogout(final String sessionToken) throws Exception {
        final MockHttpServletRequestBuilder logoutRequest = delete(URL_ROOT_SESSION)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(status().isOk());
    }
}