package com.sdadas.spring2ts.core.plugin.output.model;

import com.sdadas.spring2ts.core.plugin.output.TSOutputProcessor;
import com.sdadas.spring2ts.core.typescript.def.*;
import com.sdadas.spring2ts.core.typescript.types.CustomType;
import com.sdadas.spring2ts.core.typescript.types.TypeName;
import com.sdadas.spring2ts.core.typescript.writer.TSWritable;
import org.apache.commons.lang3.StringUtils;
import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class TSModelOutput extends TSOutputProcessor {

    public TSModelOutput(File outputDir) {
        super(outputDir);
    }

    @Override
    public TSWritable transform(JavaType<?> type) {
        if(type instanceof JavaClassSource) {
            return createClass((JavaClassSource) type);
        } else if(type instanceof JavaInterfaceSource) {
            return createInterface((JavaInterfaceSource) type);
        } else if(type instanceof JavaEnumSource) {
            return createEnum((JavaEnumSource) type);
        }
        throw new IllegalArgumentException("Unknown type: " + type.getClass().getSimpleName());
    }

    private TSWritable createClass(JavaClassSource type) {
        TSInterfaceDef ret = new TSInterfaceDef();
        TypeName parent = createTypeName(type);
        TypeContext context = new TypeContext(parent, type.getPackage());
        ret.name(parent);
        ret.extendsType(toTSType(type.getSuperType(), context));
        ret.extendsTypes(toTSTypes(type.getInterfaces(), context));
        ret.modifiers(TSModifier.EXPORT);
        
        // 添加类级别的注释
        String classComment = type.getJavaDoc() != null ? type.getJavaDoc().getFullText() : "";
        String apiModelDesc = getAnnotationValue(type, "ApiModel", "description");
        if (apiModelDesc != null && !apiModelDesc.isEmpty()) {
            classComment = (classComment + "\n" + apiModelDesc).trim();
        }
        if (!classComment.isEmpty()) {
            ret.comment(classComment);
        }
        
        ret.fields(createProperties(type.getProperties(), context));
        return ret;
    }

    private TSWritable createInterface(JavaInterfaceSource type) {
        TSInterfaceDef ret = new TSInterfaceDef();
        TypeName parent = createTypeName(type);
        TypeContext context = new TypeContext(parent, type.getPackage());
        ret.name(parent);
        ret.extendsTypes(toTSTypes(type.getInterfaces(), context));
        ret.modifiers(TSModifier.EXPORT);
        ret.fields(createProperties(type.getProperties(), context));
        return ret;
    }

    private TSWritable createEnum(JavaEnumSource type) {
        TSEnumDef ret = new TSEnumDef();
        TypeName parent = createTypeName(type);
        ret.name(parent);
        List<EnumConstantSource> constants = type.getEnumConstants();
        for (EnumConstantSource constant : constants) {
            ret.constant(constant.getName());
        }
        ret.modifier(TSModifier.EXPORT);
        return ret;
    }

    @SuppressWarnings("unchecked")
    private TypeName createTypeName(JavaType<?> type) {
        TypeName result = new CustomType(type.getName());
        if(type instanceof GenericCapableSource) {
            List<TypeVariableSource<?>> variables = ((GenericCapableSource) type).getTypeVariables();
            for (TypeVariableSource<?> variable : variables) {
                result.getGenerics().add(variable.getName());
            }
        }
        return result;
    }

    @Override
    public boolean filter(JavaType<?> type) {
        return hasAnnotation(type, "SharedModel") && isSupportedTSType(type);
    }

    @Override
    public String getFilePath(JavaType<?> type) {
        // get file name from "SharedModel" annotation's value, default to model.ts
        String name = getAnnotationValue(type, "SharedModel", "value");
        if(name == null || name.isEmpty()) {
            return "model/model.ts";
        }
        return "model/" + name + ".ts";
    }

    private String getAnnotationValue(AnnotationTarget<?> target, String annotationName, String valueName) {
        if (target == null || annotationName == null) return null;
        try {
            AnnotationSource<?> annotation = (AnnotationSource<?>) target.getAnnotation(annotationName);
            if (annotation == null) return null;
            return annotation.getStringValue(valueName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getFilePath(String name) {
        return "model/" + name;
    }

    @Override
    protected <O extends JavaSource<O>> List<TSVarDef> createProperties(List<PropertySource<O>> properties, TypeContext context) {
        List<TSVarDef> result = new ArrayList<>();
        for (PropertySource<O> property : properties) {
            TSVarDef def = new TSVarDef();
            def.name(property.getName());
            def.type(toTSType(property.getType(), context));
            
            // 添加属性级别的注释
            StringBuilder commentBuilder = new StringBuilder();
            
            // 获取字段的JavaDoc注释
            FieldSource<?> field = property.getField();
            if (field != null && field.hasJavaDoc()) {
                String fieldJavaDoc = field.getJavaDoc().getFullText();
                if (StringUtils.isNotBlank(fieldJavaDoc)) {
                    commentBuilder.append(fieldJavaDoc);
                }
            }
            
            // 获取访问器方法的JavaDoc注释
            MethodSource<?> accessor = property.getAccessor();
            if (accessor != null && accessor.hasJavaDoc()) {
                String javaDoc = accessor.getJavaDoc().getFullText();
                if (StringUtils.isNotBlank(javaDoc)) {
                    if (!commentBuilder.isEmpty()) {
                        commentBuilder.append("\n");
                    }
                    commentBuilder.append(javaDoc);
                }
            }
            
            // 获取ApiModelProperty注解的值
            String apiModelPropertyValue = getAnnotationValue(property, "ApiModelProperty", "value");
            if (apiModelPropertyValue != null && !apiModelPropertyValue.isEmpty()) {
                if (!commentBuilder.isEmpty()) {
                    commentBuilder.append("\n");
                }
                commentBuilder.append(apiModelPropertyValue);
            }
            
            String finalComment = commentBuilder.toString().trim();
            if (!finalComment.isEmpty()) {
                def.setComment(finalComment);
            }
            
            result.add(def);
        }
        return result;
    }
}
