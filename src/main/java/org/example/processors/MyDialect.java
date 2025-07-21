package org.example.processors;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class MyDialect extends AbstractProcessorDialect {

    public MyDialect() {
        super("My Dialect", "my", 1000);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new InlineCssProcessor(dialectPrefix));
        processors.add(new InlineImageProcessor(dialectPrefix));
        return processors;
    }
}
