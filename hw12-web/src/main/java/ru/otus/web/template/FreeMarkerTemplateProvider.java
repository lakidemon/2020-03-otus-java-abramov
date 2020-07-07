package ru.otus.web.template;

import freemarker.template.Configuration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
@Slf4j
public class FreeMarkerTemplateProvider implements TemplateProvider {
    private Configuration config;

    @Override
    @SneakyThrows
    public void setup(String templatesFolder) {
        log.debug("Template folder: {}", templatesFolder);
        config = new Configuration(Configuration.VERSION_2_3_30);
        config.setClassForTemplateLoading(getClass(), templatesFolder);
        config.setDefaultEncoding("UTF-8");
    }

    @Override
    public View getView(String view) {
        try {
            var template = config.getTemplate(view + ".ftl");
            return (model, to) -> {
                try {
                    template.process(model, to);
                } catch (Exception e) {
                    throw new TemplateException("Cannot render view " + view + ", model: " + model, e);
                }
            };
        } catch (IOException e) {
            throw new TemplateException("Cannot load template " + view, e);
        }
    }

}
