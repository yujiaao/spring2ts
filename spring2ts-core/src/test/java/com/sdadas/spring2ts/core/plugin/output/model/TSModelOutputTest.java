package com.sdadas.spring2ts.core.plugin.output.model;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.junit.jupiter.api.Test;

import java.io.File;


class TSModelOutputTest {

    TSModelOutput tsModelOutput = new TSModelOutput(new File("./"));

    @Test
    void getFilePath() {
        // get java type from com.sdadas.spring2ts.core.plugin.output.model.ModelTestType class
        JavaType type = Roaster.parse("package com.sdadas.spring2ts.core.plugin.output.model;\n" +
                                      "\n" +
                                      "import com.sdadas.spring2ts.annotations.SharedModel;\n" +
                                      "\n" +
                                      "@SharedModel(\"bigdata\")\n" +
                                      "public class ModelTestType {\n" +
                                      "    String name;\n" +
                                      "}\n"
        );

        System.out.println(tsModelOutput.getFilePath(type));
    }
}
