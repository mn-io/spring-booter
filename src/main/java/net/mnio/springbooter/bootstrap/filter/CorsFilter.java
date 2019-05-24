package net.mnio.springbooter.bootstrap.filter;

import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class CorsFilter implements Filter {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_KEY = "Access-Control-Allow-Origin";

    @Log
    private Logger log;

    @Value("${application.accessControlAllowOrigin}")
    private String accessControlAllowOriginValue;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        if (StringUtils.isEmpty(accessControlAllowOriginValue)) {
            throw new ServletException("application.accessControlAllowOrigin must have a value, at least '*'");
        }

        if ("*".equals(accessControlAllowOriginValue)) {
            log.warn("application.accessControlAllowOrigin is set to '*', which might cause CSRF vulnerabilities");
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        final String origin = httpRequest.getHeader("origin");
        if ("*".equals(accessControlAllowOriginValue) && origin != null) {
            // Allow-Credentials it is not allowed to be used with '*'
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS/Errors/CORSNotSupportingCredentials
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_KEY, origin);
        } else {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_KEY, accessControlAllowOriginValue);
        }

        // allows cookies within CORS
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        httpResponse.setHeader("Access-Control-Allow-Headers",
                "origin,x-requested-with,content-type,accept,withcredentials," + AuthFilter.HEADER_NAME_SESSION_TOKEN);
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Expose-Headers", AuthFilter.HEADER_NAME_SESSION_TOKEN);
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }
}
