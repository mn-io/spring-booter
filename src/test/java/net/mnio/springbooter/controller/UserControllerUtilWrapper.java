package net.mnio.springbooter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mnio.springbooter.bootstrap.filter.AuthFilter;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.controller.api.UserLoginDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerUtilWrapper {

    private final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private final String URL_ROOT_USERS = "/users";

    private final String URL_ROOT_USER_ME = "/users/me";

    private final String URL_ROOT_SESSION = "/session";
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    UserControllerUtilWrapper(final MockMvc mvc, final ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    void doAndVerifySignUp(final UserCreateOrUpdateDto createDto) throws Exception {
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

    String doAndVerifyLoginSession(final UserLoginDto loginDto) throws Exception {
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

    void doAndVerifyGetUser(final String sessionToken, final UserCreateOrUpdateDto createDto) throws
            Exception {
        final MockHttpServletRequestBuilder meRequest = get(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(meRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createDto.getName()))
                .andExpect(jsonPath("$.email").value(createDto.getEmail()));
    }

    void doAndVerifyUpdateUser(final String sessionToken, final UserCreateOrUpdateDto updateDto) throws
            Exception {
        doUpdateUser(sessionToken, updateDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }

    ResultActions doUpdateUser(final String sessionToken, final UserCreateOrUpdateDto updateDto) throws
            Exception {
        final String updatePutBody = mapper.writeValueAsString(updateDto);

        final MockHttpServletRequestBuilder meUpdateRequest = put(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken)
                .content(updatePutBody)
                .contentType(CONTENT_TYPE_JSON);
        return mvc.perform(meUpdateRequest)
                .andDo(print());
    }

    void doAndVerifyLogoutSession(final String sessionToken) throws Exception {
        final MockHttpServletRequestBuilder logoutRequest = delete(URL_ROOT_SESSION)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(status().isOk());
    }

    void doAndVerifyDeleteUser(final String sessionToken) throws Exception {
        final MockHttpServletRequestBuilder deleteRequest = delete(URL_ROOT_USER_ME)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, sessionToken);
        mvc.perform(deleteRequest)
                .andDo(print())
                .andExpect(status().isOk());
    }
}
