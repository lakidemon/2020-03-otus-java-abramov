package ru.otus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.formatter.PhoneFormatter;
import ru.otus.service.DBServiceUser;
import ru.otus.service.DefaultUserPopulatorService;
import ru.otus.service.UserPopulatorService;

@Configuration
@EnableWebMvc
@ComponentScan("ru.otus")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/", "classpath:/static/")
                .setCacheControl(CacheControl.noCache());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(phoneFormatter());
    }

    @Bean
    public PhoneFormatter phoneFormatter() {
        return new PhoneFormatter();
    }

    @Bean(initMethod = "init")
    public UserPopulatorService populatorService(DBServiceUser serviceUser) {
        return new DefaultUserPopulatorService(serviceUser);
    }
}
