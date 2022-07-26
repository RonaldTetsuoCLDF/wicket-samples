package aplicacao.db;

import java.io.Serializable;

public class Order<S> implements Serializable {
    public final S       sortProperty;
    public final boolean ascending;
    public Order(S sortProperty, boolean ascending) {
        this.sortProperty = sortProperty;
        this.ascending = ascending;
    }
    public String getOrderByWithDefault(String defaultColumn) {
        String column = (sortProperty != null) ? sortProperty.toString() : defaultColumn;
        return column + (ascending ? " asc" : " desc");
    }
}
