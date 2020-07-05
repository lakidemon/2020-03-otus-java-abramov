package ru.otus.web.template;

import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Map;

@Singleton
public class FreeMarkerTemplateProvider implements TemplateProvider {

    @Override
    public void renderPage(String view, Map<String, Object> model, PrintWriter output) {

    }
}
