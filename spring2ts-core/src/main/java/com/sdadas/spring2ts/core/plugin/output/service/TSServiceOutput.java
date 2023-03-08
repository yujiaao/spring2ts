package com.sdadas.spring2ts.core.plugin.output.service;

import com.sdadas.spring2ts.core.plugin.output.service.template.jquery.TSJQueryTemplate;
import com.sdadas.spring2ts.core.plugin.output.service.template.react.TSReactTemplate;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import com.sdadas.spring2ts.core.plugin.output.TSOutputProcessor;
import com.sdadas.spring2ts.core.plugin.output.service.template.angular2.TSAngular2Template;
import com.sdadas.spring2ts.core.plugin.output.service.template.TSServiceTemplate;
import com.sdadas.spring2ts.core.typescript.writer.TSWritable;

import java.io.File;
import java.io.IOException;

/**
 * @author Sławomir Dadas
 */
public class TSServiceOutput extends TSOutputProcessor {

    private TSServiceTemplate template;

    public TSServiceOutput(File outputDir) {
        super(outputDir);
        template = new TSReactTemplate();
        try {
            template.init(this);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public TSWritable transform(JavaType<?> type) {
        if(template instanceof TSReactTemplate) {
            return createNameSpace(type);
        }
        return createClass(type);
    }

    private TSWritable createClass(JavaType<?> type) {
        ServiceClass clazz = new ServiceClass(type);
        return this.template.serviceClass(clazz);
    }

    private TSWritable createNameSpace(JavaType<?> type) {
        ServiceClass clazz = new ServiceClass(type);
        return this.template.serviceNamespace(clazz);
    }

    @Override
    public boolean filter(JavaType<?> type) {
        if(template instanceof TSReactTemplate){
            return hasAnnotation(type, "SharedService")
                    && type.getName().contains("Controller")
                    && (type instanceof JavaInterfaceSource || type instanceof JavaClassSource);
        }

        return hasAnnotation(type,"SharedService")
                && (type instanceof JavaInterfaceSource || type instanceof JavaClassSource);
    }

    @Override
    public String getFilePath(JavaType<?> type) {
        return "service/" + type.getName() + ".ts";
    }

    @Override
    public String getFilePath(String name) {
        return "service/" + name;
    }
}
