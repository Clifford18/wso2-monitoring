package ke.co.skyworld.utils.http.mailing;

import freemarker.template.*;
import ke.co.skyworld.utils.memory.JvmManager;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemplatingEngine {

    public static String process(String templateName, Object model){
        Configuration cfg =
                new Configuration(Configuration.VERSION_2_3_28);
        // Where do we load the templates from:
        cfg.setClassForTemplateLoading(TemplatingEngine.class, "templates");

        // Some other recommended settings:
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setDefaultEncoding("UTF-8");
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
        owb.setForceLegacyNonListCollections(false);
        owb.setDefaultDateType(TemplateDateModel.DATETIME);
        cfg.setObjectWrapper(owb.build());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("model", model);
        try {
            Template template = cfg.getTemplate(templateName+".ftl");
            StringWriter out = new StringWriter();
            template.process(templateData, out);

            JvmManager.gc(cfg, owb, template, templateData, templateName, model);
            return out.getBuffer().toString();

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        JvmManager.gc(cfg, owb, templateData, templateName, model);
        return "";
    }

}
