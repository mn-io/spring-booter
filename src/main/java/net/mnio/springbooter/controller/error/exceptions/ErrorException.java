package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ErrorException extends HttpException {

    public ErrorException(final HttpStatus status, final Map<String, Object> errorAttributes) {
        super(status, errorAttributes);
    }
}
