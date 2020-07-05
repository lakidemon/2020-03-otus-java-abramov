package ru.otus.web.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.core.dao.UserDao;
import ru.otus.core.service.DBServiceUser;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.web.auth.AuthService;
import ru.otus.web.auth.InMemoryAuthService;
import ru.otus.web.filter.CharsetFilter;
import ru.otus.web.filter.LoginFilter;
import ru.otus.web.servlet.LoginServlet;
import ru.otus.web.servlet.UsersServlet;
import ru.otus.web.template.FreeMarkerTemplateProvider;
import ru.otus.web.template.TemplateProvider;
import ru.otus.web.utils.FileSystemHelper;

import javax.persistence.Persistence;

@RequiredArgsConstructor
public class JettyWebServer implements WebServer {
    private final int port;
    private Server server;
    private Injector injector;

    @Override
    public void init() {
        server = new Server(port);

        var context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        setupBeans(context);
        addServlets(context);
        addFilters(context);
        setupStaticContent(context);

        server.setHandler(context);
    }

    public static void main(String[] args) throws Exception {
        var server = new JettyWebServer(8080);
        server.init();
        server.start();
    }

    private void setupBeans(ServletContextHandler context) {
        injector = Guice.createInjector(new BasicModule());
        context.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        });
        context.addFilter(GuiceFilter.class, "/*", null);
    }

    protected void addFilters(ServletContextHandler context) {
        context.addFilter(CharsetFilter.class, "/**", null);
        context.addFilter(LoginFilter.class, "/*", null);
    }

    protected void addServlets(ServletContextHandler context) {
        context.addServlet(LoginServlet.class, "/login");
        context.addServlet(UsersServlet.class, "/");
    }

    protected void setupStaticContent(ServletContextHandler context) {
        var servletHolder = new ServletHolder(DefaultServlet.class);
        servletHolder.setInitParameter("", "true");
        servletHolder.setInitParameter("resourceBase",
                FileSystemHelper.localFileNameOrResourceNameToFullPath("static"));
        context.addServlet(servletHolder, "/static/*");
    }

    protected class BasicModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(AuthService.class).to(InMemoryAuthService.class);
            bind(TemplateProvider.class).to(FreeMarkerTemplateProvider.class);
            bind(UserDao.class).toInstance(new UserDaoHibernate(
                    new SessionManagerHibernate(Persistence.createEntityManagerFactory("otus-hibernate"))));
            bind(DBServiceUser.class).to(DbServiceUserImpl.class);
        }
    }

    @Override
    public void start() throws Exception {
        server.start();
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

}
