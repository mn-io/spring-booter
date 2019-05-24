package net.mnio.springbooter.controller.error.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    private final Map<String, Object> data;

    HttpException(final HttpStatus status) {
        this(status, null);
    }

    HttpException(final HttpStatus status, final Map<String, Object> data) {
        this.status = status;
        this.data = data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getData() {
        if (data == null) {
            return null;
        }
        return Collections.unmodifiableMap(data);
    }
}
