package net.mnio.springbooter.controller.error;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.controller.error.exceptions.BadRequestMappingException;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ErrorControllerUnitTest extends AbstractUnitTest {

    private static final String REQUEST_MAPPING_ROOT = "/errorControllerTest";

    @Test
    public void getUnknownException() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + "/exceptionUnknown"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value(RuntimeException.class.getSimpleName()));
    }

    @Test
    public void getMappedException() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + "/exceptionMapped"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value(BadRequestMappingException.class.getSimpleName()));
    }

    @Test
    public void getSpringExecption() throws Exception {
        mvc.perform(get(REQUEST_MAPPING_ROOT + "/exceptionBySpring"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value(MissingServletRequestParameterException.class.getSimpleName()
                ));
    }


    @RestController
    @RequestMapping(REQUEST_MAPPING_ROOT)
    private static class ErrorTestController {

        @PermitPublic
        @RequestMapping(value = "/exceptionUnknown", method = RequestMethod.GET)
        public ResponseEntity<Map> unknownExceptionTest() {
            if (true) {
                throw new RuntimeException("Exception handling triggered for unknown exception");
            }
            return ResponseEntity.ok().build();
        }

        @PermitPublic
        @RequestMapping(value = "/exceptionMapped", method = RequestMethod.GET)
        public ResponseEntity<Map> mappedExceptionTest() {
            if (true) {
                throw new BadRequestMappingException();
            }
            return ResponseEntity.ok().build();
        }

        @PermitPublic
        @RequestMapping(value = "/exceptionBySpring", method = RequestMethod.GET)
        public ResponseEntity<Map> springException(@RequestParam String test) {
            return ResponseEntity.ok().build();
        }
    }
}