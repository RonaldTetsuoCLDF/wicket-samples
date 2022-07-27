package aplicacao.ui;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import aplicacao.ui.cadastros.CadastrosPage;

@AuthorizeInstantiation(Roles.USER)
public class BasePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public BasePage(final PageParameters parameters) {
        super(parameters);
    }
    public BasePage(final IModel<?> model) {
        super(model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new NavbarContainer("navbarNav")
            .add(new BookmarkablePageLink<>("home", getApplication().getHomePage()))
            .add(new BookmarkablePageLink<>("cadastros", CadastrosPage.class))
            .add(new SairLink("sair")));
    }

    private final class SairLink extends Link<Void> {
        private SairLink(String id) {
            super(id);
        }
        @Override
        public void onClick() {
            AuthenticatedWebSession.get().signOut();
        }
    }

    private final class NavbarContainer extends WebMarkupContainer {
        private NavbarContainer(String id) {
            super(id);
        }
        public void onConfigure() {
        	super.onConfigure();
            setVisible(AuthenticatedWebSession.get().isSignedIn());
        }
    }
}
