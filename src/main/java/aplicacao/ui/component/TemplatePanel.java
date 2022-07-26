package aplicacao.ui.component;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class TemplatePanel extends WebMarkupContainer
    implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {

    private final SerializableFunction<TemplatePanel, String> markupSupplier;

    public TemplatePanel(String id, SerializableFunction<TemplatePanel, String> markupSupplier) {
        super(id);
        this.markupSupplier = markupSupplier;
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(false);
    }

    @Override
    public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
        return null;
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream("<wicket:panel>" + markupSupplier.apply(this) + "</wicket:panel>");
    }
}
