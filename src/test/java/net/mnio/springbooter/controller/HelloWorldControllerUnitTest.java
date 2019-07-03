package net.mnio.springbooter.controller;

import net.mnio.springbooter.AbstractUnitTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelloWorldControllerUnitTest extends AbstractUnitTest {

    @Test
    public void getHelloWorld() throws Exception {
        mvc.perform(get("/hello/world"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"data\":\"Hello World\"}"));
    }
}