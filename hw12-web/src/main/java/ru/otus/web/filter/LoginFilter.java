package ru.otus.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlets.IncludeExcludeBasedFilter;
import ru.otus.web.auth.AuthService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@Slf4j
public class LoginFilter extends IncludeExcludeBasedFilter {
    @Inject
    private AuthService authService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        if (!shouldFilter(httpRequest, httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        var session = httpRequest.getSession(false);

        var servletName = httpRequest.getServletPath();
        var isLogin = servletName.equals("/login");
        log.debug("Accessing {}", servletName);
        if (session == null) {
            if(isLogin) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect("/login");
            }
        } else {
            if(isLogin) {
                httpResponse.sendRedirect("/");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

}
