package com.sdadas.spring2ts.core;

import com.sdadas.spring2ts.core.plugin.SourceGen;

import java.io.File;
import java.io.IOException;

/**
 * @author Sławomir Dadas
 */
public class SourceGenTest {

    private final static String BASE_PATH = "/Users/xwx/projects/cjh-manager-content/";

    public static void main(String [] args) throws IOException {
        SourceGen sg = new SourceGen(
                new File(BASE_PATH + "manager/src/main/java/"),
                //new File(BASE_PATH + "typescript/"));
                new File("/Users/xwx/projects/cjh-manager-fe-react/src/services/content/SourceGen"));
        sg.run();
    }
}
