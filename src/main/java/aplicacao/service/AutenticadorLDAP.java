package aplicacao.service;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import com.google.common.collect.ImmutableMap;

public class AutenticadorLDAP {

    private String host;
    private String base;
    private String domain;

    public boolean authenticate(String username, String password) {
        try {
            final InitialDirContext context = new InitialDirContext(new Hashtable<>(
                ImmutableMap.<String, String> builder()
                    .put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
                    .put(Context.PROVIDER_URL, "ldap://" + host + "/" + base)
                    .put(Context.SECURITY_AUTHENTICATION, "simple")
                    .put(Context.SECURITY_PRINCIPAL, username + "@" + domain)
                    .put(Context.SECURITY_CREDENTIALS, password)
                    .put(Context.REFERRAL, "follow")
                    .build()));
            context.close();
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getBase() {
        return base;
    }
    public void setBase(String base) {
        this.base = base;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
