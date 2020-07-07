package ru.otus.web.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.Address;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.web.auth.AuthService;
import ru.otus.web.auth.InMemoryAuthService;
import ru.otus.web.filter.CharsetFilter;
import ru.otus.web.filter.LoginFilter;
import ru.otus.web.servlet.LoginServlet;
import ru.otus.web.servlet.LogoutServlet;
import ru.otus.web.servlet.UsersServlet;
import ru.otus.web.template.FreeMarkerTemplateProvider;
import ru.otus.web.template.TemplateProvider;
import ru.otus.web.utils.FileSystemHelper;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class JettyWebServer extends ServletModule implements WebServer {
    private final int port;
    protected Server server;
    protected Injector injector;

    @Override
    public void init() {
        server = new Server(port);

        var context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        setupGuice(context);
        configureDependencies();

        server.setHandler(context);
    }

    private void setupGuice(ServletContextHandler context) {
        injector = Guice.createInjector(this);
        context.addEventListener(new InternalGuiceServletContextListener());
        context.addFilter(GuiceFilter.class, "/*", null);
    }

    @Override
    protected void configureServlets() {
        setupBindings();
        addServlets();
        addFilters();
        setupStaticContent();
    }

    protected void setupBindings() {
        bind(AuthService.class).to(InMemoryAuthService.class);
        bind(TemplateProvider.class).to(FreeMarkerTemplateProvider.class);
        bind(SessionManager.class).to(SessionManagerHibernate.class);
        bind(EntityManagerFactory.class).toInstance(Persistence.createEntityManagerFactory("otus-hibernate"));
        bind(UserDao.class).to(UserDaoHibernate.class);
        bind(DBServiceUser.class).to(DbServiceUserImpl.class);
    }

    protected void addServlets() {
        serve("/login").with(LoginServlet.class);
        serve("/logout").with(LogoutServlet.class);
        serve("/").with(UsersServlet.class);
    }

    protected void addFilters() {
        filter("/*").through(CharsetFilter.class);
        filter("/*").through(LoginFilter.class, Map.of("excludedPaths", "/static/*,/logout,/favicon.ico"));
    }

    protected void setupStaticContent() {
        var initParams = new HashMap<String, String>();
        initParams.put("pathInfoOnly", "true");
        initParams.put("dirAllowed", "false");
        initParams.put("resourceBase", FileSystemHelper.localFileNameOrResourceNameToFullPath("static"));
        serve("/static/*").with(new DefaultServlet(), initParams);
    }

    protected void configureDependencies() {
        injector.getInstance(TemplateProvider.class).setup("/templates/");
        injector.getInstance(AuthService.class).addUser("test", "pass");

        var userService = injector.getInstance(DBServiceUser.class);
        var random = ThreadLocalRandom.current();
        for (String name : new String[] { "Вася", "Петя", "Катя", "Володя", "Иннокентий", "Авдотья" }) {
            var addresses = TimeZone.getAvailableIDs();
            var user = new User(name, random.nextInt(12, 70), new Address(addresses[random.nextInt(addresses.length)]),
                    Phone.single(String.valueOf(random.nextLong(89000000000L, 89500000000L))));
            userService.saveUser(user);
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

    private class InternalGuiceServletContextListener extends GuiceServletContextListener {

        @Override
        protected Injector getInjector() {
            return injector;
        }
    }
}
