package com.sdadas.spring2ts.core.plugin.output.service.template.react;

import com.sdadas.spring2ts.core.plugin.output.TypeMapper;
import com.sdadas.spring2ts.core.plugin.output.service.method.ServiceMethod;
import com.sdadas.spring2ts.core.plugin.output.service.template.base.TSBaseMethodTemplate;
import com.sdadas.spring2ts.core.plugin.output.service.template.base.TSRequestBuilder;
import com.sdadas.spring2ts.core.typescript.types.CustomType;
import com.sdadas.spring2ts.core.typescript.types.TypeName;

/**
 * @author Sławomir Dadas
 */
public class TSReactMethodTemplate extends TSBaseMethodTemplate {

    public TSReactMethodTemplate(ServiceMethod method, TypeMapper typeMapper) {
        super(method, typeMapper);
    }

    @Override
    protected TSRequestBuilder createRequestBuilder() {
        return new TSReactRequestBuilder();
    }

    @Override
    protected TypeName createMethodReturnType() {
        TypeName tn = super.createMethodReturnType();
        return tn.isVoid()
                ? new CustomType("Promise", "API.RequestResult<any>")
                : new CustomType("Promise", "API.RequestResult<"+tn.toDeclaration()+">");
    }
}
