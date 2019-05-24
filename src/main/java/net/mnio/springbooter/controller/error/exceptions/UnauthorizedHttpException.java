package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedHttpException extends HttpException {

    public UnauthorizedHttpException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
