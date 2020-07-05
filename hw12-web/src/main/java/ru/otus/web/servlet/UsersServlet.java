package ru.otus.web.servlet;

import ru.otus.core.service.DBServiceUser;
import ru.otus.web.template.TemplateProvider;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;

public class UsersServlet extends HttpServlet {
    @Inject
    private DBServiceUser userService;
    @Inject
    private TemplateProvider templates;
}
