package org.example.processors;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

public class InlineImageProcessor extends AbstractElementTagProcessor  {
    private static final String TAG_NAME = "inlineimg";
    private static final int PRECEDENCE = 1000;

    public InlineImageProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);
    }



    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String src = tag.getAttributeValue("src");

        // Resolve expressions like ${...} if needed
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(context.getConfiguration());
        Object evaluated = parser.parseExpression(context, src).execute(context);
        String resolvedSrc = String.valueOf(evaluated);

        // Read and encode image
        String base64Image;
        String contentType;

        try {
            byte[] imageBytes = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resolvedSrc)).readAllBytes();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);

            if (resolvedSrc.endsWith(".png")) {
                contentType = "image/png";
            } else if (resolvedSrc.endsWith(".jpg") || resolvedSrc.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (resolvedSrc.endsWith(".gif")) {
                contentType = "image/gif";
            } else {
                contentType = "application/octet-stream";
            }

        } catch (IOException e) {
            base64Image = "";
            contentType = "text/plain";
        }

        // Create <img src="data:image/png;base64,..."/>
        IModelFactory modelFactory = context.getModelFactory();
        IModel imgModel = modelFactory.createModel();

        imgModel.add(modelFactory.createStandaloneElementTag(
                "img",
                "src", "data:" + contentType + ";base64," + base64Image,
                false,
                true
        ));

        structureHandler.replaceWith(imgModel, false);
    }


}
