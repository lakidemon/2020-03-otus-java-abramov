package ru.otus.web.template;

import java.io.PrintWriter;
import java.util.Map;

public interface TemplateProvider {

    void setup(String templatesFolder);

    View getView(String view);

    interface View {
        void render(Map<String, Object> model, PrintWriter to);

        default void render(PrintWriter to) {
            render(null, to);
        }
    }
}
