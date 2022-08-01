package aplicacao.ui.home;

import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import aplicacao.ui.BasePage;

@AuthorizeInstantiation({})
public class LoginPage extends BasePage {

    private String username;
    private String password;

    public LoginPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new FeedbackPanel("feedback"));

        Form<?> form = new Form<LoginPage>("form", new CompoundPropertyModel<>(this)) {
            @Override
            protected void onSubmit() {
                if (Strings.isEmpty(username))
                    return;

                boolean authResult = AuthenticatedWebSession.get().signIn(username, password);
                //if authentication succeeds redirect user to the requested page
                if (authResult)
                    continueToOriginalDestination();
            }
        };

        form.add(new TextField<>("username")
            .setLabel(Model.of("Usuário"))
            .setRequired(true));
        form.add(new PasswordTextField("password")
            .setLabel(Model.of("Senha"))
            .setRequired(true));

        add(form);
    }
    
    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (AuthenticatedWebSession.get().isSignedIn())
            throw new RestartResponseException(Application.get().getHomePage());
    }
}
