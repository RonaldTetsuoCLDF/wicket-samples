package aplicacao.ui.component.behavior;

import static aplicacao.ui.Utils.*;
import static org.springframework.web.util.JavaScriptUtils.*;

import org.apache.wicket.Component;
import org.danekja.java.util.function.serializable.SerializableSupplier;

public class ConfirmationBehavior extends OnDomReadyBehavior {

    public ConfirmationBehavior(String event, SerializableSupplier<String> msg) {
        super(c -> $(c) + ".on('" + event + "', function(){ return confirm('" + javaScriptEscape(msg.get()) + "'); })");
    }
    @Override
    public boolean isEnabled(Component component) {
        return component.isVisibleInHierarchy() && component.isEnabledInHierarchy();
    }
}
