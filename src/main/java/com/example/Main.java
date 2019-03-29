package com.example;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.bidi.support.ICUBreakers;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.io.IOUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private TemplateEngine thymeleaf = new TemplateEngine();

    public static void main(String[] args) throws Exception {

        File targetDir = new File("target/pdf-out");

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File targetFile = new File(targetDir, "out.pdf");
        OutputStream output = new FileOutputStream(targetFile);
        Map<String, String> model = new HashMap<>();

        new Main().render(model, output);

    }


    private void render(Map<String, String> model, OutputStream outputStream) throws Exception {

        String html = createHtmlTickets(model);

        convertHtmlToPdf(outputStream, html);
    }

    private String createHtmlTickets(Map<String, String> model) throws IOException {
        String template = IOUtils.toString(Main.class.getResource("/template.xhtml"), StandardCharsets.UTF_8);

        return thymeleaf.process(template, createContext(model));
    }

    private void convertHtmlToPdf(OutputStream outputStream, String html) throws Exception {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, Main.class.getResource("/root.htm").toExternalForm());
        builder.toStream(outputStream);
        builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
        builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
        builder.useUnicodeCharacterBreaker(new ICUBreakers.ICULineBreaker(java.util.Locale.ENGLISH));
        builder.run();
    }

    private Context createContext(Map<String, String> model) {
        Context context = new Context();
        context.setVariable("model", model);
        return context;
    }

}
