package ru.otus.web.servlet;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.security.AbstractLoginService;
import ru.otus.web.auth.AuthService;
import ru.otus.web.template.TemplateProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Singleton
@Slf4j
public class LoginServlet extends HttpServlet {
    @Inject
    private AuthService authService;
    @Inject
    private TemplateProvider templates;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = req.getParameterMap().containsKey("error") ? Map.of("failed", true) : null;
        templates.getView("login").render(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var username = req.getParameter("username");
        var password = req.getParameter("password");

        if (username != null && password != null) {
            authService.authenticate(username, password)
                    .ifPresentOrElse(admin -> createSession(admin, req, resp), () -> redirectToError(resp));
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @SneakyThrows
    private void createSession(AbstractLoginService.UserPrincipal admin, HttpServletRequest req,
            HttpServletResponse resp) {
        var session = req.getSession(true);
        session.setAttribute("username", admin.getName());

        resp.sendRedirect("/");
    }

    @SneakyThrows
    private void redirectToError(HttpServletResponse response) {
        response.sendRedirect("/login?error=true");
    }
}
