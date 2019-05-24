package net.mnio.springbooter.controller;

import net.mnio.springbooter.AbstractIntegrationTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractIntegrationTest {

    @Test
    public void test() throws Exception {
        mvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}