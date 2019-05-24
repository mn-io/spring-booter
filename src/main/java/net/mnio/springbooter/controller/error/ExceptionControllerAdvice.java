package net.mnio.springbooter.controller.error;

import net.mnio.springbooter.controller.error.exceptions.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static net.mnio.springbooter.controller.error.ErrorHandlerComponent.LogLevel;


@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    private ErrorHandlerComponent errorHandlerComponent;

    @ExceptionHandler(HttpException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleHttpException(final HttpException ex) {
        return errorHandlerComponent.handleException(ex, LogLevel.DEBUG);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    protected ResponseEntity<Object> handleCatchAll(final Throwable ex) {
        return errorHandlerComponent.handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR);
    }


//    @ExceptionHandler(DataIntegrityViolationException.class)
//    @ResponseBody
//    protected ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException ex) {
//        final ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
//        String constrainName = constraintViolationException.getConstraintName();
//
//        final Throwable cause = ex.getMostSpecificCause();
////        if (constrainName == null && cause instanceof MySQLIntegrityConstraintViolationException) {
////            final String message = cause.getMessage();
////            constrainName = StringUtils.substringBetween(message, "CONSTRAINT `", "` FOREIGN KEY");
////        }
//
//        final String type = TYPE_PREFIX_DB_CONSTRAINT + constrainName;
//
//        final JsonExceptionWrapper exceptionWrapper = new JsonExceptionWrapper(type);
//
//        final String msg = String.format(LOG_FORMAT, "Handle DataIntegrityViolationException", exceptionWrapper.getUniqueCode(), type);
//        log.debug(msg, ex);
//
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionWrapper);
//    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return errorHandlerComponent.handleException(ex, status, LogLevel.ERROR);
    }
}
