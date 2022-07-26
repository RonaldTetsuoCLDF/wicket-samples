package aplicacao.ui.cadastros;

import javax.inject.Inject;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import aplicacao.db.PessoaDAO;
import aplicacao.to.PessoaTO;
import aplicacao.ui.BasePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class EditarPessoaPage extends BasePage {

    @Inject
    private PessoaDAO pessoaDAO;

    public EditarPessoaPage(final PageParameters parameters) {
        super(parameters);
        final CompoundPropertyModel<PessoaTO> model = new CompoundPropertyModel<>(Model.of());

        final Long id = parameters.get("id").toOptionalLong();
        final boolean alteracao = id != null;
        if (alteracao) {
            model.setObject(pessoaDAO.select(id));
        } else {
            model.setObject(new PessoaTO());
        }

        add(new FeedbackPanel("feedback"));

        Form<PessoaTO> form = new Form<>("form", model);
        add(form);

        form.add(new Label("title", (alteracao) ? "Editar pessoa" : "Nova pessoa"));
        form.add(new TextField<>("nome").setLabel(Model.of("Nome")).setRequired(true));
        form.add(new BookmarkablePageLink<>("cancelar", PessoasPage.class));
        form.add(new Button("salvar") {
            @Override
            public void onSubmit() {
                if (alteracao) {
                    pessoaDAO.update(model.getObject());
                    getSession().success("Registro alterado com sucesso!");
                } else {
                    pessoaDAO.insert(model.getObject());
                    getSession().success("Registro incluído com sucesso!");
                }
                setResponsePage(PessoasPage.class);
            }
        });
    }
}
