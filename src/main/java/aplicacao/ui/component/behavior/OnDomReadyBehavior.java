package aplicacao.ui.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class OnDomReadyBehavior extends Behavior {

    private SerializableFunction<Component, CharSequence> script;

    public OnDomReadyBehavior(SerializableFunction<Component, CharSequence> script) {
        this.script = script;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(script.apply(component)));
    }
}
