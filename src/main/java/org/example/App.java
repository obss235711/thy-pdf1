package org.example;

import org.example.processors.MyDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception{
        TemplateEngine templateEngine = templateEngine();

        String reportAsHtml = createHTML(templateEngine);

        convertAndSavePdf(reportAsHtml);


    }

    public static void convertAndSavePdf(String reportHtml) throws Exception {

        String body = "{\"html\":" + reportHtml + "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/html2pdf/content"))
                .header("Content-Type", "text/html")
                .POST(HttpRequest.BodyPublishers.ofString(reportHtml))
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        System.out.println("Status code: " + response.statusCode());
        Files.write(Paths.get("C:\\dev\\temp\\test.pdf"),response.body());
    }

    public static String createHTML(
            TemplateEngine templateEngine
    ) {
        // Prepare context with variables
        Context context = new Context();
        context.setVariable("name", "Alice");
        context.setVariable("imageSrc", "templates/img/thy.png");

        // Process template to string
        return templateEngine.process("template1", context);
    }

    public static TemplateEngine templateEngine() {
        // Setup Template Resolver
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");

        // Create Template Engine
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new MyDialect());

        return templateEngine;
    }
}
