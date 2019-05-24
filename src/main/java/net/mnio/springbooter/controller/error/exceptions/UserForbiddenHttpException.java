package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

public class UserForbiddenHttpException extends HttpException {

    public UserForbiddenHttpException() {
        super(HttpStatus.FORBIDDEN);
    }
}
