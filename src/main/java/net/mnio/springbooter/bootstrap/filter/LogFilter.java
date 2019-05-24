package net.mnio.springbooter.bootstrap.filter;

import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
Using an additional log filter instead of RequestInterceptor
because this is applied first in the execution chain.
 */
@Component
@Order(0)
public class LogFilter implements Filter {

    @Log
    private Logger log;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        log.debug("Incoming request: {} ({})", httpRequest.getRequestURL(), httpRequest.getMethod());
        chain.doFilter(request, response);
    }
}
