package net.mnio.springbooter.controller;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.controller.error.exceptions.BadRequestMappingException;
import org.junit.Test;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HelloWorldControllerTest extends AbstractUnitTest {

    @Test
    public void getHelloWorld() throws Exception {
        mvc.perform(get("/hello/world"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"data\":\"Hello World\"}"));
    }

    @Test
    public void getUnknownException() throws Exception {
        mvc.perform(get("/hello/exceptionUnknown"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value(RuntimeException.class.getSimpleName()));
    }

    @Test
    public void getMappedException() throws Exception {
        mvc.perform(get("/hello/exceptionMapped"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value(BadRequestMappingException.class.getSimpleName()));
    }

    @Test
    public void getSpringExecption() throws Exception {
        mvc.perform(get("/hello/exceptionBySpring"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value(MissingServletRequestParameterException.class.getSimpleName()
                ));
    }
}