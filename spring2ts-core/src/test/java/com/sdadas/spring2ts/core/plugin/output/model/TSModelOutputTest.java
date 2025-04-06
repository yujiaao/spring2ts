package com.sdadas.spring2ts.core.plugin.output.model;

import com.sdadas.spring2ts.core.typescript.def.TSVarDef;
import com.sdadas.spring2ts.core.typescript.types.BasicType;
import com.sdadas.spring2ts.core.typescript.types.CustomType;
import com.sdadas.spring2ts.core.typescript.types.TypeName;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

public class TSModelOutputTest {

    @Test
    public void testFieldCommentConversion() {
        // 准备测试数据
        String sourceCode = """
                package com.sdadas.spring2ts.examples.simple;
                
                import com.sdadas.spring2ts.annotations.SharedModel;
                import io.swagger.annotations.ApiModelProperty;
                
                @SharedModel
                public class HelloResponse {
                
                    /**
                     * 这里是id注释
                     */
                    @ApiModelProperty(value = "这里是id注解")
                    private int id;
                
                    private String greeting;
                
                    public HelloResponse() {
                    }
                
                    public HelloResponse(int id, String greeting) {
                        this.id = id;
                        this.greeting = greeting;
                    }
                
                    /**
                     * 获取id
                     */
                    public int getId() {
                        return id;
                    }
                
                    public void setId(int id) {
                        this.id = id;
                    }
                
                    public String getGreeting() {
                        return greeting;
                    }
                
                    public void setGreeting(String greeting) {
                        this.greeting = greeting;
                    }
                }""";

        // 解析Java源代码
        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        
        // 创建TSModelOutput实例
        TSModelOutput modelOutput = new TSModelOutput(new File("target/test-output")) {
            @Override
            public TypeName toTSType(Type<?> type, TypeContext context) {
                return BasicType.NUMBER;
            }
            
            @Override
            public boolean filter(JavaType<?> type) {
                return true;
            }
            
            @Override
            public String getFilePath(JavaType<?> type) {
                return "test.ts";
            }
            
            @Override
            public String getFilePath(String name) {
                return "test.ts";
            }
        };
        
        // 创建TypeContext
        TypeName parent = new CustomType("HelloResponse");
        TypeContext context = new TypeContext(parent, "com.sdadas.spring2ts.examples.simple");
        
        // 转换Java类为TypeScript接口
        List<TSVarDef> properties = modelOutput.createProperties(javaClass.getProperties(), context);
        
        // 验证id字段的注释
        TSVarDef idProperty = properties.stream()
                .filter(p -> p.getName().equals("id"))
                .findFirst()
                .orElse(null);
                
        assertNotNull(idProperty, "id字段应该存在");
        assertNotNull(idProperty.getComment(), "id字段应该有注释");
        assertTrue(idProperty.getComment().contains("获取id"), "注释应该包含JavaDoc注释");
        assertTrue(idProperty.getComment().contains("这里是id注解"), "注释应该包含ApiModelProperty注解的值");
    }
}
