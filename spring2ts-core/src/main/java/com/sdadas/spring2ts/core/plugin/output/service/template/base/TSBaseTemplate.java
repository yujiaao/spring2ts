package com.sdadas.spring2ts.core.plugin.output.service.template.base;

import com.sdadas.spring2ts.core.plugin.output.service.CommentAnnotationUtils;
import com.sdadas.spring2ts.core.plugin.output.service.method.ServiceRequestProps;
import com.sdadas.spring2ts.core.typescript.types.VarType;
import org.apache.commons.io.IOUtils;

import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.springframework.core.io.ClassPathResource;
import com.sdadas.spring2ts.core.plugin.output.OutputProcessor;
import com.sdadas.spring2ts.core.plugin.output.TypeMapper;
import com.sdadas.spring2ts.core.plugin.output.service.ServiceClass;
import com.sdadas.spring2ts.core.plugin.output.service.method.ServiceMethod;
import com.sdadas.spring2ts.core.plugin.output.service.template.TSServiceTemplate;
import com.sdadas.spring2ts.core.typescript.def.*;
import com.sdadas.spring2ts.core.typescript.writer.TSWritable;
import com.sdadas.spring2ts.core.typescript.types.BasicType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public abstract class TSBaseTemplate implements TSServiceTemplate {

    public static final String CONTROLLER_PREFIX = "cp";
    public static final String SERVICE_PREFIX = "prefix";
    protected TypeMapper typeMapper;


    @Override
    public void init(TypeMapper mapper) throws IOException {
        this.typeMapper = mapper;
        onInit();
    }

    protected abstract void onInit() throws IOException;

    protected void afterCreateClass(TSClassDef clazz) {
    }

    protected void afterCreateMethod(TSFunctionDef method) {

    }

    protected void loadOutputFile(TypeMapper mapper, String classPath) throws IOException {
        if(mapper instanceof OutputProcessor) {
            ClassPathResource resource = new ClassPathResource(classPath);
            OutputProcessor processor = (OutputProcessor) mapper;
            String path = processor.getFilePath(resource.getFilename());
            TSFile file = processor.createNewFile(path);
            file.loadContent(IOUtils.toString(resource.getInputStream(),  Charset.defaultCharset()));
        }
    }

    @Override
    public TSWritable serviceClass(ServiceClass clazz) {
        TSClassDef res = createClass(clazz);
        afterCreateClass(res);
        Set<String> importUsed = new HashSet<>();
        for (ServiceMethod method : clazz.getMethods()) {
            TSFunctionDef func = createMethod(method);
            afterCreateMethod(func);
            res.function(func);

            String requestMethod = func.getImportUsed();

            if(requestMethod!=null) {
                importUsed.add(requestMethod);
            }
        }

        createImports(importUsed);
        return res;
    }

    protected TSFunctionDef createMethod(ServiceMethod method) {
        return new TSBaseMethodTemplate(method, typeMapper).createFunction();
    }

    private TSClassDef createClass(ServiceClass clazz) {
        JavaType<?> type = clazz.getType();
        TSClassDef res = new TSClassDef();
        res.name(type.getName());
        res.modifier(TSModifier.EXPORT);
        return res;
    }

    private TSNameSpaceDef createNameSpace(ServiceClass clazz) {
        JavaType<?> type = clazz.getType();
        TSNameSpaceDef res = new TSNameSpaceDef();
        res.name(type.getName());
        res.modifier(TSModifier.EXPORT);

       String comment = CommentAnnotationUtils.extractedComment(type);

        res.comment(comment);
        return res;
    }

    protected void createImports(Set<String> importUsed) {
        typeMapper.imports("Observable", "rxjs/Observable");
        typeMapper.imports(new TSImport("rxjs/Rx"));
        typeMapper.imports("RequestBuilder", "./RequestBuilder");
        typeMapper.imports("HttpMethods", "./RequestBuilder");
        typeMapper.imports("ContentTypes", "./RequestBuilder");
    }

    protected void afterCreateNameSpace(TSNameSpaceDef clazz) {
    }
    @Override
    public TSWritable serviceNamespace(ServiceClass clazz) {

        TSNameSpaceDef res = createNameSpace(clazz);
        ServiceRequestProps props = ServiceMethod.createProps(clazz.getType());

        res.globalVariable(new TSVarDef(CONTROLLER_PREFIX, BasicType.STRING,
                " "+SERVICE_PREFIX+" + '"+(props.getPaths().isEmpty()?"":props.getPaths().get(0).getPath())+"'")
                .varType(VarType.CONST));

        afterCreateNameSpace(res);
        Set<String> importUsed = new HashSet<>();


        Set<String> controllerAnnotations = new HashSet<>(
                Arrays.asList("RequestMapping", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping")
        );

        for (ServiceMethod method : clazz.getMethods()) {

            boolean needExport = false;
            for(AnnotationSource<?> ann:  method.getMethod().getAnnotations()){
                if (controllerAnnotations.contains(ann.getName())){
                    needExport = true;
                    break;
                }
            }
            if(!needExport){
                continue;
            }

            TSFunctionDef func = createMethod(method);
            afterCreateMethod(func);
            res.function(func);

            String requestMethod = func.getImportUsed();

            if(requestMethod!=null) {
                System.out.println("function name="+ func.getName()+ "import: "+requestMethod);
                importUsed.add(requestMethod);
            }

        }
        createImports(importUsed);
        return res;
    }

}
