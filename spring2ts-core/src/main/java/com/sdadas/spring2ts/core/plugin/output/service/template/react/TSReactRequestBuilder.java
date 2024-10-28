package com.sdadas.spring2ts.core.plugin.output.service.template.react;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sdadas.spring2ts.core.plugin.output.service.template.base.TSRequestBuilder;
import com.sdadas.spring2ts.core.typescript.types.TypeName;
import com.sdadas.spring2ts.core.typescript.writer.CodeWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.of;
import static com.sdadas.spring2ts.core.plugin.output.service.template.base.TSBaseTemplate.CONTROLLER_PREFIX;

/**
 * @author Sławomir Dadas
 */
public class TSReactRequestBuilder extends TSRequestBuilder {

    /**
     *
     * @param cw
     * @throws IOException
     */
    @Override
    public void write(CodeWriter cw) throws IOException {
        cw.openIndent();
        cw.writeln("const url = "+CONTROLLER_PREFIX+" + '"+path+"';");

        String query = params.values().stream()
                .filter(p-> p.is(ParamType.Query)|| p.is(ParamType.Model))
                .map(param -> param.is(ParamType.Query) || param.javaType.isPrimitive() ? param.value : "..."+ param.value)
//                .map(param ->  param.value)
                .collect(Collectors.joining(","));

        String queryUri = "const query='?'+"+params.values().stream()
                .filter(p-> p.is(ParamType.Query) )
                .map(param -> "'"+param.value+"='+encodeURIComponent("+ param.value+")" ).collect(Collectors.joining("+'&'+"));

        String body = params.values().stream()
                .filter(p-> p.is(ParamType.Body))
                .map(param -> "..."+param.value).collect(Collectors.joining(","));

        if(!StringUtils.isEmpty(query) && !StringUtils.isEmpty(body)){
            cw.writeln(queryUri);
        }
        cw.writeln("return ");

        writeMethod(cw);

        boolean bodyIsArray = params.values().stream().anyMatch(p-> p.is(ParamType.Body) && p.isArrayType());

        if(StringUtils.isEmpty(query+body)){
            cw.write("url);");
        }else if(StringUtils.isEmpty(body)){
            cw.write("url, {").write(query).write("});");
        }else if(StringUtils.isEmpty(query)){
            if(bodyIsArray) {
                cw.write("url, [").write(body).write("]);");
            }  else {
                cw.write("url, {").write(body).write("});");
            }
        }else {
            // query + body
            if(bodyIsArray) {
                cw.write("url + query, [").write(body).write("]);");
            }else {
                cw.write("url + query, {").write(body).write("});");
            }
        }



        /*
        invoke(cw, "queryParam", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Query));
        invoke(cw, "queryParams", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Query));
        invoke(cw, "pathVariable", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Path));
        invoke(cw, "pathVariables", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Path));
        invoke(cw, "matrixVariable", p -> p.hasName() && !p.hasPathVar() && p.is(TSRequestBuilder.ParamType.Matrix));
        invoke(cw, "matrixVariables", p -> !p.hasName() && !p.hasPathVar() && p.is(TSRequestBuilder.ParamType.Matrix));
        invoke(cw, "pathMatrixVariable", p -> p.hasName() && p.hasPathVar() && p.is(TSRequestBuilder.ParamType.Matrix));
        invoke(cw, "pathMatrixVariables", p -> !p.hasName() && p.hasPathVar() && p.is(TSRequestBuilder.ParamType.Matrix));
        invoke(cw, "header", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Header));
        invoke(cw, "headers", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Header));
        */


        //writeBody(cw);
        //writeContentType(cw);

        cw.closeIndent();
    }



    private void handleModelAttributes(CodeWriter cw) throws IOException {
        Optional<TSRequestBuilder.Param> param = getHttpMethodParam();
        List<TSRequestBuilder.Param> models = params.values().stream().filter(p -> p.is(TSRequestBuilder.ParamType.Model)).collect(Collectors.toList());
        if(param.isPresent()) {
            cw.write("if (%s.payload)").openBlock();
            cw.write("_builder");
            invoke(cw, "bodyPart", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Model));
            invoke(cw, "bodyParts", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Model));
            cw.closeBlock();
            cw.write("else").openBlock();
            cw.write("_builder");
            invoke(cw, "queryParam", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Model));
            invoke(cw, "queryParams", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Model));
            cw.closeBlock();
        } else {
            TSRequestBuilder.ParamType newType = isPayloadMethod(this.method) ? TSRequestBuilder.ParamType.Body : TSRequestBuilder.ParamType.Query;
            models.forEach(m -> m.type = newType);
        }
    }

    private boolean isPayloadMethod(RequestMethod method) {
        return EnumSet.of(RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH).contains(method);
    }

    private void writeMethod(CodeWriter cw) throws IOException {
        String methodName = getMethodName();
        cw.write(methodName).write("(");
    }


    private void writeBody(CodeWriter cw) throws IOException {
        if(countBodyParts() > 1) {
            invoke(cw, "bodyPart", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Body));
            invoke(cw, "bodyParts", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Body));
        } else {
            invoke(cw, "body", p -> !p.hasName() && p.is(TSRequestBuilder.ParamType.Body));
            invoke(cw, "body", p -> p.hasName() && p.is(TSRequestBuilder.ParamType.Body), p -> {
                return new String[] {String.format("{%s: %s}", p.name, p.value)};
            });
        }
    }

    private long countBodyParts() {
        return params.values().stream().filter(p -> p.is(TSRequestBuilder.ParamType.Body)).count();
    }

    @Override
    public String getName() {
        return null;
    }

    private void invoke(CodeWriter writer, String methodName, Predicate<TSRequestBuilder.Param> filter,
                        Function<TSRequestBuilder.Param, String[]> arguments) throws IOException {

        List<String[]> results = params.values().stream()
                .filter(filter).map(arguments)
                .collect(Collectors.toList());
        for (String[] args : results) {
            writer.writeln(".").write(methodName)
                    .write('(').write(Joiner.on(", ").useForNull("null").join(args)).write(")");
        }
    }

    private void invoke(CodeWriter writer, String methodName, Predicate<TSRequestBuilder.Param> filter) throws IOException {
        invoke(writer, methodName, filter, this::defaultArgs);
    }

    private String[] defaultArgs(TSRequestBuilder.Param param) {
        List<String> res = Lists.newArrayList();
        if(param.hasPathVar()) res.add(param.pathVar);
        if(param.hasName()) res.add(String.format("'%s'", param.name));
        res.add(param.value);
        return res.toArray(new String[res.size()]);
    }


}
