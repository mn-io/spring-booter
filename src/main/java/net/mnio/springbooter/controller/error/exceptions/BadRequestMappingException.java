package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestMappingException extends HttpException {
    public BadRequestMappingException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
