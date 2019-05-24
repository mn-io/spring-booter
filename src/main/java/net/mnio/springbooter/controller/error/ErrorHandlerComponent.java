package net.mnio.springbooter.controller.error;

import net.mnio.springbooter.controller.api.ExceptionDto;
import net.mnio.springbooter.controller.error.exceptions.HttpException;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlerComponent {

    enum LogLevel {DEBUG, INFO, ERROR}

    @Log
    private Logger log;

    ResponseEntity<Object> handleException(final HttpException t, final LogLevel logLevel) {
        final ExceptionDto dto = new ExceptionDto(t.getClass().getSimpleName(), t.getStatus(), t.getData());
        logException(dto, logLevel, t);
        return ResponseEntity.status(t.getStatus()).body(dto);
    }

    ResponseEntity<Object> handleException(final Throwable t, final HttpStatus status, final LogLevel logLevel) {
        final ExceptionDto dto = new ExceptionDto(t.getClass().getSimpleName(), status);
        logException(dto, logLevel, t);
        return ResponseEntity.status(status).body(dto);
    }

    private void logException(final ExceptionDto dto, final LogLevel logLevel, final Throwable t) {
        final String format = String.format("%s (exception id: %s, http status: %s)", dto.getName(), dto.getId(), dto.getStatus());
        switch (logLevel) {
            case DEBUG:
                log.debug(format, t);
                break;
            case INFO:
                log.info(format);
                break;
            case ERROR:
                log.error(format, t);
                break;
        }
    }
}
