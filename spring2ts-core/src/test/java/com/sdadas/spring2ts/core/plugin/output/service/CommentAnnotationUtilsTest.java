package com.sdadas.spring2ts.core.plugin.output.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommentAnnotationUtils} - verifies compatibility with
 * both Swagger 1.x and Springdoc (Swagger 3.x) annotations.
 */
public class CommentAnnotationUtilsTest {

    @Test
    public void testSwagger1ApiOperationOnMethod() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.annotations.Api;
                import io.swagger.annotations.ApiOperation;
                
                @Api("Test Controller")
                public class TestController {
                
                    @ApiOperation(value = "获取用户信息", notes = "根据ID获取用户详细信息")
                    public String getUser() {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        Multimap<String, String> map = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(javaClass, map);

        assertTrue(map.containsKey("comment"), "Should contain comment key for @Api");
        String comment = map.get("comment").toString();
        assertTrue(comment.contains("Test Controller"), "Comment should contain @Api value");
    }

    @Test
    public void testSwagger1ApiParamOnParameter() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.annotations.ApiOperation;
                import io.swagger.annotations.ApiParam;
                
                public class TestController {
                
                    @ApiOperation(value = "获取用户")
                    public String getUser(
                            @ApiParam(value = "用户ID", required = true) String userId,
                            @ApiParam(value = "用户名称") String userName) {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        var methods = javaClass.getMethods();
        var method = methods.get(0);
        var params = method.getParameters();

        // Test first parameter
        Multimap<String, String> map1 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(params.get(0), map1);
        assertTrue(map1.containsKey("description"), "Should contain description for @ApiParam");
        String desc1 = map1.get("description").toString();
        assertTrue(desc1.contains("用户ID"), "Description should contain @ApiParam value");

        // Test second parameter
        Multimap<String, String> map2 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(params.get(1), map2);
        assertTrue(map2.containsKey("description"), "Should contain description for @ApiParam");
        String desc2 = map2.get("description").toString();
        assertTrue(desc2.contains("用户名称"), "Description should contain @ApiParam value");
    }

    @Test
    public void testSpringdocOperationOnMethod() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.v3.oas.annotations.Operation;
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Tag(name = "用户管理", description = "用户相关接口")
                public class TestController {
                
                    @Operation(summary = "获取用户列表", description = "分页获取所有用户")
                    public String listUsers() {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        Multimap<String, String> map = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(javaClass, map);

        assertTrue(map.containsKey("comment"), "Should contain comment key for @Tag");
        String comment = map.get("comment").toString();
        assertTrue(comment.contains("用户管理"), "Comment should contain @Tag name");
    }

    @Test
    public void testSpringdocParameterOnParam() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.v3.oas.annotations.Operation;
                import io.swagger.v3.oas.annotations.Parameter;
                
                public class TestController {
                
                    @Operation(summary = "查询用户")
                    public String queryUser(
                            @Parameter(description = "用户唯一标识") String id,
                            @Parameter(description = "用户姓名", required = true) String name) {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        var methods = javaClass.getMethods();
        var method = methods.get(0);
        var params = method.getParameters();

        // Test first parameter
        Multimap<String, String> map1 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(params.get(0), map1);
        assertTrue(map1.containsKey("description"), "Should contain description for @Parameter");
        String desc1 = map1.get("description").toString();
        assertTrue(desc1.contains("用户唯一标识"), "Description should contain @Parameter description");

        // Test second parameter
        Multimap<String, String> map2 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(params.get(1), map2);
        assertTrue(map2.containsKey("description"), "Should contain description for @Parameter");
        String desc2 = map2.get("description").toString();
        assertTrue(desc2.contains("用户姓名"), "Description should contain @Parameter description");
    }

    @Test
    public void testSpringdocSchemaOnField() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.v3.oas.annotations.media.Schema;
                
                public class UserDto {
                
                    @Schema(description = "用户ID", example = "12345")
                    private String id;
                    
                    @Schema(description = "用户姓名", example = "张三")
                    private String name;
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        var fields = javaClass.getFields();

        // Test first field
        Multimap<String, String> map1 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(fields.get(0), map1);
        assertTrue(map1.containsKey("description"), "Should contain description for @Schema");
        String desc1 = map1.get("description").toString();
        assertTrue(desc1.contains("用户ID"), "Description should contain @Schema description");

        // Test second field
        Multimap<String, String> map2 = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(fields.get(1), map2);
        assertTrue(map2.containsKey("description"), "Should contain description for @Schema");
        String desc2 = map2.get("description").toString();
        assertTrue(desc2.contains("用户姓名"), "Description should contain @Schema description");
    }

    @Test
    public void testMixedSwaggerAndSpringdocAnnotations() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.annotations.Api;
                import io.swagger.annotations.ApiOperation;
                import io.swagger.v3.oas.annotations.Operation;
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Api("老版接口")
                @Tag(name = "新版接口", description = "兼容新旧注解")
                public class MixedController {
                
                    @ApiOperation(value = "旧版获取用户", notes = "使用Swagger1注解")
                    @Operation(summary = "新版获取用户", description = "使用Springdoc注解")
                    public String getUser() {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);

        // Test class-level annotations
        Multimap<String, String> classMap = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(javaClass, classMap);
        assertTrue(classMap.containsKey("comment"), "Should contain comment for class-level annotations");
        String classComment = classMap.get("comment").toString();
        assertTrue(classComment.contains("老版接口"), "Should contain @Api value");
        assertTrue(classComment.contains("新版接口"), "Should contain @Tag name");
    }

    @Test
    public void testExtractedCommentWithJavaDoc() {
        String sourceCode = """
                package com.test;
                
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                /**
                 * 获取用户信息
                 * 详细描述
                 */
                @Tag(name = "用户管理", description = "用户相关接口")
                public class TestController {
                
                    public String getUser() {
                        return "test";
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        String result = CommentAnnotationUtils.extractedComment((org.jboss.forge.roaster.model.JavaType<?>) javaClass);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("获取用户信息"), "Should contain JavaDoc text");
        assertTrue(result.contains("用户管理"), "Should contain @Tag name");
    }

    @Test
    public void testNoAnnotations() {
        String sourceCode = """
                package com.test;
                
                public class SimpleClass {
                    private String name;
                    
                    public String getName() {
                        return name;
                    }
                }""";

        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        Multimap<String, String> map = HashMultimap.create();
        CommentAnnotationUtils.extractedComment(javaClass, map);

        assertTrue(map.isEmpty(), "Should be empty when no annotations present");
    }

    @Test
    public void testToStringMethod() {
        Multimap<String, String> map = HashMultimap.create();
        map.put("summary", "测试摘要");
        map.put("description", "测试描述");

        String result = CommentAnnotationUtils.toString(map);
        assertTrue(result.contains("测试描述"), "Should contain description value");
        assertTrue(result.contains("summary:测试摘要"), "Should contain key:value format for non-omitable keys");
    }
}