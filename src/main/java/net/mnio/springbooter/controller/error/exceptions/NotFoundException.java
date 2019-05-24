package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Collections;

public class NotFoundException extends HttpException {

    public NotFoundException(final String requestUri) {
        super(HttpStatus.NOT_FOUND, Collections.singletonMap("uri", requestUri));
    }
}
