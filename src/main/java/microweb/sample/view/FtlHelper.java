package microweb.sample.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringWriter;

public abstract class FtlHelper {
    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);

    // Perform the entire configuration here, similarly to what is presented in
    // FreeMarker tutorial, at https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html:
    static {
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);

        //
        // Templates will be loaded from Classpath, to turn the project self-contained and packable
        //
        configuration.setClassForTemplateLoading(FtlHelper.class, "/views");
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    // Just a helper to process templates to string easily:
    public static String processToString(Template tpl, Object dataModel) throws Exception {
        StringWriter writer = new StringWriter();
        tpl.process(dataModel, writer);
        return writer.toString();
    }
}
