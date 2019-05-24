package net.mnio.springbooter.bootstrap.filter;

import net.mnio.springbooter.bootstrap.session.PermitPublic;
import net.mnio.springbooter.bootstrap.session.UserSessionContext;
import net.mnio.springbooter.controller.error.exceptions.BadRequestMappingException;
import net.mnio.springbooter.controller.error.exceptions.UnauthorizedHttpException;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Order(2)
public class AuthFilter implements Filter {

    public static final String HEADER_NAME_SESSION_TOKEN = "X-Session-Token";

    private static final Map<Method, Boolean> PERMIT_CACHE = new HashMap<>(1);

    @Log
    private Logger log;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        final HandlerExecutionChain handler;
        try {
            handler = requestMappingHandlerMapping.getHandler(httpRequest);
        } catch (final Exception e) {
            log.error("Error due to request mapping", e);
            handlerExceptionResolver.resolveException(httpRequest, httpResponse, null, new BadRequestMappingException());
            return;
        }

        //handle unknown mappings (usually spring internal) as public
        final boolean isEndpointMapped = handler != null;
        if (!isEndpointMapped) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        final boolean isSwagger = httpRequest.getServletPath().startsWith("/swagger-resources");
        if (isSwagger) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        final boolean isPublicMethod = hasPublicAnnotation(handler);
        if (isPublicMethod) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        final String sessionToken = httpRequest.getHeader(HEADER_NAME_SESSION_TOKEN);
        if (StringUtils.isEmpty(sessionToken)) {
            handlerExceptionResolver.resolveException(httpRequest, httpResponse, null, new UnauthorizedHttpException());
            return;
        }

        final Optional<UserSession> userSession = UserSessionContext.buildInstance(userSessionRepository, sessionToken);
        if (userSession.isEmpty()) {
            handlerExceptionResolver.resolveException(httpRequest, httpResponse, null, new UnauthorizedHttpException());
            return;
        }

        chain.doFilter(httpRequest, httpResponse);
    }

    private static boolean hasPublicAnnotation(final HandlerExecutionChain handler) {
        final Method method = ((HandlerMethod) handler.getHandler()).getMethod();
        Boolean hasAnnotation = PERMIT_CACHE.get(method);
        if (hasAnnotation == null) {
            final PermitPublic permission = method.getDeclaredAnnotation(PermitPublic.class);
            hasAnnotation = permission != null;
            PERMIT_CACHE.put(method, hasAnnotation);
        }
        return hasAnnotation;
    }
}
