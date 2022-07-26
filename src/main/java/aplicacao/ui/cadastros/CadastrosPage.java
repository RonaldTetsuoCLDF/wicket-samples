package aplicacao.ui.cadastros;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import aplicacao.ui.BasePage;

public class CadastrosPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public CadastrosPage(final PageParameters parameters) {
        super(parameters);

        add(new BookmarkablePageLink<>("pessoas", PessoasPage.class));
    }
}
