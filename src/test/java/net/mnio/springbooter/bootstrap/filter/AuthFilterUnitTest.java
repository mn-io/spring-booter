package net.mnio.springbooter.bootstrap.filter;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.controller.error.exceptions.UnauthorizedHttpException;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthFilterUnitTest extends AbstractUnitTest {

    private static final String REQUEST_MAPPING_ROOT = "/authFilterTest";
    private static final String REQUEST_MAPPING_PUBLIC = "/public";
    private static final String REQUEST_MAPPING_PRIVATE = "/private";
    private final String REQUEST_PARAM_VALUE = "test";
    private final String REQUEST_PARAM = "?echo=" + REQUEST_PARAM_VALUE;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Test
    public void isPublic() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + REQUEST_MAPPING_PUBLIC + REQUEST_PARAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(REQUEST_PARAM_VALUE));
    }

    @Test
    public void isNonPublic() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + REQUEST_MAPPING_PRIVATE + REQUEST_PARAM))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.name").value(UnauthorizedHttpException.class.getSimpleName()));
    }

    @Test
    public void isNonPublicWithFakeToken() throws Exception {
        final MockHttpServletRequestBuilder request = get(REQUEST_MAPPING_ROOT + REQUEST_MAPPING_PRIVATE + REQUEST_PARAM)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, "something");
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.name").value(UnauthorizedHttpException.class.getSimpleName()));
    }

    @Test
    public void isNonPublicWithCorrectToken() throws Exception {
        final User user = new User();
        final UserSession userSession = new UserSession();
        userSession.generateToken();
        userSession.setUser(user);
        when(userSessionRepository.findByToken(userSession.getToken())).thenReturn(Optional.of(userSession));

        final MockHttpServletRequestBuilder request = get(REQUEST_MAPPING_ROOT + REQUEST_MAPPING_PRIVATE + REQUEST_PARAM)
                .header(AuthFilter.HEADER_NAME_SESSION_TOKEN, userSession.getToken());
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(REQUEST_PARAM_VALUE));

        verify(userSessionRepository, times(1)).findByToken(userSession.getToken());
    }

    @Test
    public void isNotFound() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + "/notFound"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @RestController
    @RequestMapping(REQUEST_MAPPING_ROOT)
    private static class PermissionController {

        @PermitPublic
        @RequestMapping(value = REQUEST_MAPPING_PUBLIC, method = RequestMethod.GET)
        public ResponseEntity<String> isPublic(@RequestParam String echo) {
            return ResponseEntity.ok().body(echo);
        }

        @RequestMapping(value = REQUEST_MAPPING_PRIVATE, method = RequestMethod.GET)
        public ResponseEntity<String> isPrivate(@RequestParam String echo) {
            return ResponseEntity.ok().body(echo);
        }
    }
}