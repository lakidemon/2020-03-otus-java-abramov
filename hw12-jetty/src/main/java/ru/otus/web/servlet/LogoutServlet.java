package ru.otus.web.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@Slf4j
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var ses = req.getSession(false);
        if (ses != null) {
            log.debug("Logging out user " + ses.getAttribute("username"));
            ses.invalidate();
            resp.sendRedirect("/");
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
