package org.example.processors;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class InlineCssProcessor extends AbstractElementTagProcessor {
    private static final String TAG_NAME = "inlinecss";
    private static final int PRECEDENCE = 1000;

    public InlineCssProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);
    }



    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String href = tag.getAttributeValue("href");
        if (href == null) {
            structureHandler.removeElement();
            return;
        }

        // Read CSS file content (example: from classpath or file system)
        String css = readCssFile(href);

        IModelFactory modelFactory = context.getModelFactory();
        IModel styleModel = modelFactory.createModel();
        styleModel.add(modelFactory.createOpenElementTag("style"));
        styleModel.add(modelFactory.createText("\n"+css+"\n"));
        styleModel.add(modelFactory.createCloseElementTag("style"));

        structureHandler.replaceWith(styleModel, false);
    }

    private String readCssFile(String href) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(href);
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + href);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
