package net.mnio.springbooter.controller;

import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.controller.error.exceptions.BadRequestMappingException;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @Log
    private Logger log;

    @PermitPublic
    @RequestMapping(value = "/world", method = RequestMethod.GET)
    public ResponseEntity<Map> hello() {
        log.info("Hello World :)");
        return ResponseEntity.ok().body(Collections.singletonMap("data", "Hello World"));
    }

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
    public ResponseEntity<Map> springExecption(@RequestParam String test) {
        log.info("Request parameter: {}", test);
        return ResponseEntity.ok().build();
    }
}
