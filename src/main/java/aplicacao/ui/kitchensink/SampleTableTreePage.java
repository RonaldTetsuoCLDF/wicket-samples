package aplicacao.ui.kitchensink;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableTreeProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import aplicacao.ui.BasePage;
import aplicacao.ui.component.ContainerColumn;
import aplicacao.ui.kitchensink.SelectableTableTree.ISelectionProvider;

public class SampleTableTreePage extends BasePage {

    List<ContaFuncionalNode>  contas                  = new ArrayList<>(Arrays.asList(              //
        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00208", "Empenhado", 350.00))                           //
                .add(new EmpenhoNode("2022NE00422", "Empenhado", 350.00)))                          //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00208", "Empenhado", 350.00))                           //
                .add(new EmpenhoNode("2022NE00422", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00229", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00))),                         //

        new ContaFuncionalNode("1.01.122.8204.2619.9711.100.0.3.3.90.93", "Provisionado", 350.00)   //
            .add(new CredorNode("UG-FUNDO DE ASSISTÊNCIA À SAÚDE", "Autorizado", 350.00)            //
                .add(new EmpenhoNode("2022NE00232", "Empenhado", 350.00)))                          //

    ));

    IModel<Set<AbstractNode>> empenhoSelecionadoModel = Model.ofSet(new HashSet<>());

    public SampleTableTreePage(final PageParameters parameters) {
        super(parameters);

        SortableTreeProvider<AbstractNode, Void> treeProvider = new SortableTreeProvider<AbstractNode, Void>() {
            @Override
            public Iterator<? extends AbstractNode> getRoots() {
                return contas.iterator();
            }

            @Override
            public boolean hasChildren(AbstractNode node) {
                return !(node instanceof EmpenhoNode);
            }

            @Override
            public Iterator<? extends AbstractNode> getChildren(AbstractNode node) {
                return (Iterator<? extends AbstractNode>) node.children.iterator();
            }

            @Override
            public IModel<AbstractNode> model(AbstractNode object) {
                return Model.of(object);
            }
        };
        List<IColumn<AbstractNode, Void>> columns = new ArrayList<>();
        columns.add(new TreeColumn<>(Model.of("Conta Funcional/Credor")));
        columns.add(new LambdaColumn<>(Model.of("Situação"), it -> it.situacao));
        columns.add(new LambdaColumn<>(Model.of("Valor Desp."), it -> DecimalFormat.getCurrencyInstance().format(it.valor)));
        columns.add(new ContainerColumn<AbstractNode, Void>(Model.of("Ações"), //
            (cid, m) -> (m.getObject() instanceof CredorNode)
                ? this.newAcoesFragment(cid, m)
                : new Label(cid, "")));

        SelectableTableTree<AbstractNode, Void> tabletree = //
            new SelectableTableTree<>("tabletree", columns, treeProvider, 1000,
                ISelectionProvider.multipleSelection(empenhoSelecionadoModel, it -> it instanceof EmpenhoNode))
                    .setAutoExpandSingleChild(true);

        add(new Link<Void>("abrirTodos") {
            @Override
            public void onClick() {
                tabletree.expandAll();
            }
        });

        add(new Link<Void>("fecharTodos") {
            @Override
            public void onClick() {
                tabletree.collapseAll();
            }
        });

        add(new FeedbackPanel("feedback"));
        add(tabletree);
    }

    public Component newAcoesFragment(String componentId, IModel<AbstractNode> rowModel) {
        return new Fragment(componentId, "AcoesFragment", this)
            .add(new Link<AbstractNode>("moverEmpenhoSelecionado", rowModel) {
                @Override
                public void onClick() {
                    Iterator<AbstractNode> itsel = empenhoSelecionadoModel.getObject().iterator();
                    while (itsel.hasNext()) {
                        EmpenhoNode empenhoSelecionado = (EmpenhoNode) itsel.next();
                        contas.stream()
                            .flatMap(it -> it.children.stream())
                            .filter(it -> it.children.contains(empenhoSelecionado))
                            .forEach(it -> it.children.remove(empenhoSelecionado));
                        ;
                        this.getModelObject().children.add(empenhoSelecionado);
                    }
                }
            });
    }

    static abstract class AbstractNode implements Serializable {
        String             descricao;
        String             situacao;
        double             valor;
        List<AbstractNode> children = new ArrayList<>();

        public AbstractNode(String descricao, String situacao, double valor) {
            this.descricao = descricao;
            this.situacao = situacao;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return descricao;
        }
        List<? extends AbstractNode> children() {
            return (List<? extends AbstractNode>) children;
        }
    }

    static class ContaFuncionalNode extends AbstractNode {
        public ContaFuncionalNode(String descricao, String situacao, double valor) {
            super(descricao, situacao, valor);
        }

        ContaFuncionalNode add(CredorNode credor) {
            children.add(credor);
            return this;
        }
    }

    static class CredorNode extends AbstractNode {
        public CredorNode(String descricao, String situacao, double valor) {
            super(descricao, situacao, valor);
        }

        CredorNode add(EmpenhoNode empenho) {
            children.add(empenho);
            return this;
        }
    }

    static class EmpenhoNode extends AbstractNode {
        public EmpenhoNode(String descricao, String situacao, double valor) {
            super(descricao, situacao, valor);
        }
    }
}