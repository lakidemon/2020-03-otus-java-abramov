package ru.otus.web.template;

import java.io.PrintWriter;
import java.util.Map;

public interface TemplateProvider {

    void renderPage(String view, Map<String, Object> model, PrintWriter output);

}
