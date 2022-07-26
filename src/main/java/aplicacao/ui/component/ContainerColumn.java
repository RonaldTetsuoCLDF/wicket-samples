package aplicacao.ui.component;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public class ContainerColumn<T, S> extends AbstractColumn<T, S> {

    private IComponentFactory<T> childFactory;

    public ContainerColumn(IModel<String> displayModel, S sortProperty, IComponentFactory<T> childFactory) {
        super(displayModel, sortProperty);
        this.childFactory = childFactory;
    }

    public ContainerColumn(IModel<String> displayModel, IComponentFactory<T> childFactory) {
        super(displayModel);
        this.childFactory = childFactory;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(childFactory.newComponent(componentId, rowModel));
    }

    public interface IComponentFactory<T> extends Serializable {
        Component newComponent(String componentId, IModel<T> rowModel);
    }
}
