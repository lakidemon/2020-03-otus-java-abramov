package ru.otus.web.auth;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.util.security.Password;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class InMemoryAuthService implements AuthService {
    private Map<String, AbstractLoginService.UserPrincipal> admins = new HashMap<>();

    @Override
    public Optional<AbstractLoginService.UserPrincipal> authenticate(String username, String password) {
        return Optional.ofNullable(admins.get(username.toLowerCase())).filter(p -> p.authenticate(password));
    }

    @Override
    public AbstractLoginService.UserPrincipal addUser(String name, String password) {
        var user = new AbstractLoginService.UserPrincipal(name, new Password(password));
        admins.put(name.toLowerCase(), user);
        return user;
    }
}
