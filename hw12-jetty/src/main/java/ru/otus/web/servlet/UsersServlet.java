package ru.otus.web.servlet;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import ru.otus.core.model.Address;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;
import ru.otus.web.template.TemplateProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class UsersServlet extends HttpServlet {
    @Inject
    private DBServiceUser userService;
    @Inject
    private TemplateProvider templates;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templates.getView("users").render(Map.of("users", userService.getAll()), resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name, street;
        String[] phones;
        int age;

        try {
            name = getValidatedParameter(req, "name");
            age = Integer.valueOf(getValidatedParameter(req, "age", UsersServlet::isInteger));
            street = getValidatedParameter(req, "address");
            phones = req.getParameterValues("phone");
        } catch (ServletException e) {
            log.warn("Passed invalid parameters: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var user = new User(name, age, new Address(street),
                Arrays.stream(phones).map(Phone::new).collect(Collectors.toList()));
        userService.saveUser(user);

        resp.sendRedirect("/");
    }

    String getValidatedParameter(HttpServletRequest request, String name, Predicate<String>... validations)
            throws ServletException {
        var param = request.getParameter(name);
        if (!Strings.isNullOrEmpty(param)) {
            for (Predicate<String> validation : validations) {
                if (!validation.apply(param)) {
                    throw new ServletException("Invalid param " + name + ": " + param);
                }
            }
            return param;
        }
        throw new ServletException("Param " + name + " is empty");
    }

    static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
