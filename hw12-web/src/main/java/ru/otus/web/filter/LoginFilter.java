package ru.otus.web.filter;

import ru.otus.web.auth.AuthService;

import javax.inject.Inject;
import javax.servlet.*;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Inject
    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
