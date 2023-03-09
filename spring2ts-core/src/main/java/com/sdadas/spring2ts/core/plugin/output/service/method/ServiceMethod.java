package com.sdadas.spring2ts.core.plugin.output.service.method;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.sdadas.spring2ts.core.plugin.output.service.params.ServiceParamType;
import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.springframework.web.bind.annotation.*;
import com.sdadas.spring2ts.core.plugin.output.service.params.ServiceParam;
import com.sdadas.spring2ts.core.utils.AnnotationUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class ServiceMethod {

    private final JavaType<?> type;

    private final MethodSource<?> method;

    private final ServiceRequestProps props;

    private final List<ServiceParam> params;

    public ServiceMethod(JavaType<?> type, MethodSource<?> method) {
        this.type = type;
        this.method = method;
        ServiceRequestProps globalProps = createProps(type);
        ServiceRequestProps methodProps = createProps(method);
        this.props = methodProps.merged(globalProps);
        this.params = createParams(method);
    }

    static  void findAnnotationAsMap(Multimap<String,String> target, AnnotationTarget<?> type, Class ... annotations) {
        Arrays.stream(annotations).forEach(annotation ->{
            Multimap<String, String> map1 = AnnotationUtils.getAnnotationAsMap(type, annotation);
            if(map1!=null) target.putAll(map1);
        });
    }
    public static ServiceRequestProps createProps(AnnotationTarget<?> type) {
        Multimap<String, String> mapAll = ArrayListMultimap.create();
        findAnnotationAsMap(mapAll, type, RequestMapping.class, GetMapping.class, PostMapping.class);

        ServiceRequestProps props = new ServiceRequestProps(mapAll);
        props.setResponseBody(AnnotationUtils.hasAny(type, ResponseBody.class, RestController.class));
        return props;
    }

    private List<ServiceParam> createParams(MethodSource<?> method) {
        List<? extends ParameterSource<?>> parameters = method.getParameters();
        List<ServiceParam> results = Lists.newArrayList();
        parameters.stream().filter(parameter -> ServiceParamType.resolve(parameter) != ServiceParamType.IGNORED)
                .forEach(p -> results.add(new ServiceParam(p)));
        return results;
    }
    public String getName() {
        return method.getName();
    }

    public JavaType<?> getType() {
        return type;
    }

    public MethodSource<?> getMethod() {
        return method;
    }

    public List<ServiceParam> getParams() {
        return params;
    }

    public ServiceRequestProps getProps() {
        return props;
    }

    public boolean requiredPayload() {
        for (ServiceParam param : params) {
            if(param.isPayload()) return true;
        }
        return false;
    }
}
