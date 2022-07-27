package aplicacao.ui;

import javax.servlet.ServletContext;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.mapper.PackageMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.resource.JQueryResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.PackageName;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import aplicacao.ui.cadastros.PessoasPage;
import aplicacao.ui.home.HomePage;
import aplicacao.ui.home.LoginPage;
import aplicacao.ui.kitchensink.SampleTableTreePage;

@Component
public class WicketApplication extends AuthenticatedWebApplication {

    @Override
    public void init() {
        super.init();

        getMarkupSettings().setDefaultMarkupEncoding("utf-8");
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setStripWicketTags(true);

        getJavaScriptLibrarySettings().setJQueryReference(JQueryResourceReference.getV3());

//        getComponentInstantiationListeners()
//            .add(c -> c.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
        getComponentInstantiationListeners()
            .add(new SpringComponentInjector(this));

        mountPackage("login", LoginPage.class);
        mountPackage("cadastros", PessoasPage.class);
        mountPackage("kitchensink", SampleTableTreePage.class);
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return WicketSession.class;
    }

    @Override
    public <P extends Page> PackageMapper mountPackage(String path, Class<P> pageClass) {
        PackageMapper packageMapper = new PackageMapper(path, PackageName.forClass(pageClass)) {
            @Override
            protected String transformForUrl(String className) {
                return StringUtils.delete(className, "Page");
            }
            @Override
            protected String transformFromUrl(String classNameAlias) {
                return classNameAlias + "Page";
            }
        };
        mount(packageMapper);
        return packageMapper;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    public <T> T bean(Class<T> beanClass) {
        final ServletContext servletContext = this.getServletContext();
        final WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return applicationContext.getBean(beanClass);
    }

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }
}
