package aplicacao.ui;

import java.util.Locale;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

public class WicketSession extends AuthenticatedWebSession {

    private Roles roles = new Roles();

    public WicketSession(Request request) {
        super(request);
        setLocale(new Locale("pt", "BR"));
    }

    @Override
    protected boolean authenticate(String username, String password) {
        roles.clear();

        boolean authenticated = true; // WicketApplication.get().bean(AutenticadorLDAP.class).authenticate(username, password);

        if (authenticated)
            roles.add(Roles.USER);

        if ("admin".equals(username))
            roles.add(Roles.ADMIN);

        return authenticated;
    }

    @Override
    public Roles getRoles() {
        return roles;
    }
}
