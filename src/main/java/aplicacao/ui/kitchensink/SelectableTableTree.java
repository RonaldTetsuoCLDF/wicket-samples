package aplicacao.ui.kitchensink;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssPackageResource;
import org.apache.wicket.request.resource.CssResourceReference;
import org.danekja.java.util.function.serializable.SerializablePredicate;

public class SelectableTableTree<T, S> extends TableTree<T, S> {

    private ISelectionProvider<T> selectionProvider;
    private boolean               autoExpandSingleChild = false;

    public SelectableTableTree(String id, List<? extends IColumn<T, S>> columns, ISortableTreeProvider<T, S> provider, int rowsPerPage) {
        this(id, columns, provider, rowsPerPage, null, null);
    }
    public SelectableTableTree(String id, List<? extends IColumn<T, S>> columns, ISortableTreeProvider<T, S> provider, int rowsPerPage, IModel<? extends Set<T>> state) {
        this(id, columns, provider, rowsPerPage, state, null);
    }
    public SelectableTableTree(String id, List<? extends IColumn<T, S>> columns, ISortableTreeProvider<T, S> provider, int rowsPerPage, ISelectionProvider<T> selectionProvider) {
        this(id, columns, provider, rowsPerPage, null, selectionProvider);
    }
    public SelectableTableTree(String id, List<? extends IColumn<T, S>> columns, ISortableTreeProvider<T, S> provider, int rowsPerPage, IModel<? extends Set<T>> state, ISelectionProvider<T> selectionProvider) {
        super(id, columns, provider, rowsPerPage, state);
        this.selectionProvider = selectionProvider;
        add(AttributeModifier.append("class", "SelectableTableTree"));

        setUpDefaults(provider);
    }

    protected void setUpDefaults(ISortableTreeProvider<T, S> provider) {
        getTable().addTopToolbar(new NavigationToolbar(getTable()));
        getTable().addTopToolbar(new HeadersToolbar<>(getTable(), provider));
        getTable().addBottomToolbar(new NoRecordsToolbar(getTable()));

        add(new WindowsTheme());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CssResourceReference.of(
            SelectableTableTree.class.getSimpleName() + ".css",
            () -> new CssPackageResource(
                SelectableTableTree.class,
                SelectableTableTree.class.getSimpleName() + ".css",
                getLocale(), null, null))));
    }

    public SelectableTableTree<T, S> setAutoExpandSingleChild(boolean autoExpandSingleChild) {
        this.autoExpandSingleChild = autoExpandSingleChild;
        return this;
    }
    public boolean isAutoExpandSingleChild() {
        return autoExpandSingleChild;
    }

    public void expandAll() {
        Deque<T> stack = new LinkedList<>();
        Consumer<Iterator<? extends T>> pushAll = it -> {
            while (it.hasNext())
                stack.push(it.next());
        };

        ITreeProvider<T> provider = this.getProvider();
        pushAll.accept(provider.getRoots());

        while (!stack.isEmpty()) {
            T node = stack.pop();
            expand(node);
            pushAll.accept((Iterator<? extends T>) provider.getChildren(node));
        }
    }

    public void collapseAll() {
        Iterator<? extends T> it = getProvider().getRoots();
        while (it.hasNext())
            collapse(it.next());
    }

    @Override
    public void collapse(T t) {
        super.collapse(t);
        if (getSelectionProvider().isPresent()) {
            ISelectionProvider<T> sp = getSelectionProvider().get();
            ITreeProvider<T> tp = getProvider();

            Deque<Iterator<? extends T>> stack = new LinkedList<>();
            stack.push(tp.getChildren(t));

            while (!stack.isEmpty()) {
                Iterator<? extends T> it = stack.pop();
                while (it.hasNext()) {
                    T node = it.next();
                    sp.unselect(node);
                    stack.push(tp.getChildren(node));
                }
            }
        }
    }

    @Override
    public void expand(T t) {
        super.expand(t);
        if (isAutoExpandSingleChild()) {
            Iterator<? extends T> it = getProvider().getChildren(t);
            if (it.hasNext()) {
                T firstChild = it.next();
                if (!it.hasNext()) {
                    this.expand(firstChild);
                }
            }
        }
    }

    public Optional<ISelectionProvider<T>> getSelectionProvider() {
        return Optional.ofNullable(selectionProvider);
    }

    @Override
    protected Component newContentComponent(String id, IModel<T> model) {
        return new Folder<T>(id, this, model) {
            @Override
            protected void onClick(Optional<AjaxRequestTarget> targetOptional) {
                T modelObject = this.getModelObject();
                if (getSelectionProvider().isPresent()) {
                    ISelectionProvider<T> sp = getSelectionProvider().get();
                    if (sp.isSelectable(modelObject)) {
                        if (sp.isSelected(modelObject)) {
                            sp.unselect(modelObject);
                        } else {
                            sp.select(modelObject);
                        }
                    }
                }
                super.onClick(targetOptional);
            }

            @Override
            protected boolean isSelected() {
                return getSelectionProvider()
                    .map(it -> it.isSelected(this.getModelObject()))
                    .orElse(false);
            }
            @Override
            protected boolean isClickable() {
                return getSelectionProvider().isPresent();
            }
        };
    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> node) {
        Item<T> newRowItem = new OddEvenItem<>(id, index, node);
        newRowItem.add(new AttributeAppender("class", //
            () -> getSelectionProvider().map(it -> it.isSelected(node.getObject())).orElse(false) //
                ? getString(Folder.SELECTED_CSS_CLASS_KEY) //
                : "", //
            " "));
        return newRowItem;
    }

    public interface ISelectionProvider<T> extends IDetachable {
        boolean isSelected(T object);
        void select(T object);
        void unselect(T object);
        default boolean isSelectable(T object) {
            return true;
        }
        @Override
        default void detach() {
        }

        static <T> ISelectionProvider<T> singleSelection(IModel<T> selectionModel, SerializablePredicate<T> isSelectable) {
            return new ISelectionProvider<T>() {
                @Override
                public boolean isSelectable(T object) {
                    return isSelectable.test(object);
                }
                @Override
                public boolean isSelected(T object) {
                    return selectionModel.getObject() == object;
                }
                @Override
                public void select(T object) {
                    selectionModel.setObject(object);
                }
                @Override
                public void unselect(T object) {
                    if (selectionModel.getObject() == object)
                        selectionModel.setObject(null);
                }
            };
        }

        static <T> ISelectionProvider<T> multipleSelection(IModel<? extends Set<T>> selectionModel, SerializablePredicate<T> isSelectable) {
            return new ISelectionProvider<T>() {
                @Override
                public boolean isSelectable(T object) {
                    return isSelectable.test(object);
                }
                @Override
                public boolean isSelected(T object) {
                    return selectionModel.getObject().contains(object);
                }
                @Override
                public void select(T object) {
                    selectionModel.getObject().add(object);
                }
                @Override
                public void unselect(T object) {
                    selectionModel.getObject().remove(object);
                }
            };
        }
    }
}
