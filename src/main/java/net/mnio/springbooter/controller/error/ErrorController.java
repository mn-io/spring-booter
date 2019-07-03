package net.mnio.springbooter.controller.error;

import net.mnio.springbooter.controller.error.exceptions.ErrorException;
import net.mnio.springbooter.controller.error.exceptions.NotFoundException;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static net.mnio.springbooter.controller.error.ErrorHandlerComponent.LogLevel;

/*
 * see: https://stackoverflow.com/questions/35957040/how-to-show-custom-404-page-when-user-enters-invalid-url-in-spring-boot-applicat
 */
@RestController
public class ErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";

    @Log
    private Logger log;

    @Autowired
    private ErrorHandlerComponent errorHandlerComponent;

    @Autowired
    public ErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<Object> handleErrors(final HttpServletRequest request) {
        final HttpStatus status = getStatus(request);

        if (status == HttpStatus.NOT_FOUND) {
            final String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
            final NotFoundException exception = new NotFoundException(requestUri);
            return errorHandlerComponent.handleException(exception, LogLevel.DEBUG);
        }

        // if ExceptionControllerAdvice has an error
        final Map<String, Object> errorAttributes = getErrorAttributes(request, true);
        final ErrorException exception = new ErrorException(status, errorAttributes);
        return errorHandlerComponent.handleException(exception, status, LogLevel.ERROR);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}