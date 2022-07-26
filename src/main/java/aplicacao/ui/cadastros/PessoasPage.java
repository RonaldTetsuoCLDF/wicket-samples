package aplicacao.ui.cadastros;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import aplicacao.db.PessoaDAO;
import aplicacao.to.PessoaTO;
import aplicacao.ui.BasePage;
import aplicacao.ui.Utils;
import aplicacao.ui.component.ContainerColumn;
import aplicacao.ui.component.behavior.ConfirmationBehavior;

public class PessoasPage extends BasePage {

    @Inject
    private PessoaDAO pessoaDAO;

    public PessoasPage(final PageParameters parameters) {
        super(parameters);
        final PessoasDataProvider dataProvider = new PessoasDataProvider();
        dataProvider.getSortState().setPropertySortOrder("nome", SortOrder.ASCENDING);

        final List<IColumn<PessoaTO, String>> columns = Arrays.asList(
            new LambdaColumn<>(Model.of("ID"), "id", pes -> pes.getId()),
            new LambdaColumn<>(Model.of("Nome"), "lower(nome)", pes -> pes.getNome()),
            new ContainerColumn<>(Model.of("Ações"), (id, m) -> new AcoesFragment(id, m)));

        add(new FeedbackPanel("feedback"));
        add(new DefaultDataTable<>("lista", columns, dataProvider, 10));
        add(new BookmarkablePageLink<>("novo", EditarPessoaPage.class));
    }

    @AuthorizeAction(action = Action.ENABLE, roles = Roles.ADMIN)
    private class AcoesFragment extends Fragment {
        public AcoesFragment(String id, IModel<PessoaTO> model) {
            super(id, "AcoesFragment", PessoasPage.this);

            add(new BookmarkablePageLink<>("editar",
                EditarPessoaPage.class, new PageParameters().add("id", model.getObject().getId())));

            add(new Link<PessoaTO>("excluir", model) {
                @Override
                public void onClick() {
                    pessoaDAO.delete(this.getModelObject().getId());
                    getSession().success("Registro removido com sucesso!");
                }
            }.add(new ConfirmationBehavior("click", () -> "Excluir '" + model.getObject().getNome() + "'?")));
        }
    }

    private class PessoasDataProvider extends SortableDataProvider<PessoaTO, String> {
        @Override
        public long size() {
            return pessoaDAO.countAll();
        }
        @Override
        public Iterator<? extends PessoaTO> iterator(long first, long count) {
            return pessoaDAO.selectAll(Utils.toOrder(getSort()), first, count).iterator();
        }
        @Override
        public IModel<PessoaTO> model(PessoaTO object) {
            return Model.of(object);
        }
    }
}
