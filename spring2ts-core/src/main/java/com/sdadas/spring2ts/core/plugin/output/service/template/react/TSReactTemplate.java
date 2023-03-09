package com.sdadas.spring2ts.core.plugin.output.service.template.react;

import com.sdadas.spring2ts.core.plugin.output.service.method.ServiceMethod;
import com.sdadas.spring2ts.core.plugin.output.service.template.base.TSBaseTemplate;
import com.sdadas.spring2ts.core.typescript.def.TSFunctionDef;

import java.io.IOException;

/**
 * @author Sławomir Dadas
 */
public class TSReactTemplate extends TSBaseTemplate {

    @Override
    protected void onInit() throws IOException {
        // loadOutputFile(typeMapper, "plugin/output/service/react/RequestBuilder.ts");
    }

    @Override
    protected TSFunctionDef createMethod(ServiceMethod method) {
        return new TSReactMethodTemplate(method, typeMapper).createFunction();
    }


    protected void createImports() {
        String request="@/utils/request";
        //import { post, get, postJson } from '@/utils/request';
        typeMapper.imports("post", request);
        typeMapper.imports("get", request);
        typeMapper.imports("postJson", request);

        String constant="../constant";
        //import { post, get, postJson } from '@/utils/request';
        typeMapper.imports(SERVICE_PREFIX , constant);

    }
}
