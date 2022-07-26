package aplicacao.ui;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import aplicacao.db.Order;

public abstract class Utils {
    private Utils() {}

    public static <S> Order<S> toOrder(SortParam<S> sort) {
        return new Order<>(sort.getProperty(), sort.isAscending());
    }

    public static String $(Component comp) {
        comp.setOutputMarkupId(true);
        return "$('#" + comp.getMarkupId() + "')";
    }
}
