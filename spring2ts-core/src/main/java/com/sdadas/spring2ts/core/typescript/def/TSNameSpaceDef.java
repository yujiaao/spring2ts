package com.sdadas.spring2ts.core.typescript.def;

import com.sdadas.spring2ts.core.typescript.writer.CodeWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class TSNameSpaceDef extends TSTypeDef<TSNameSpaceDef> {

    List<TSVarDef> globalVariables = new ArrayList<>();


    @Override
    public void writeNameDef(CodeWriter writer) throws IOException {
        writeGlobalVariables(writer);
        writer.writeln();

        writer.write("export const ");
        writer.write(name.toDeclaration());
        writer.write(" = ");
    }

    public TSNameSpaceDef globalVariable(TSVarDef var) {
          globalVariables.add(var);
          return this;
    }


    public void writeGlobalVariables(CodeWriter writer) {
        if(globalVariables == null || globalVariables.isEmpty()) return;
        globalVariables.forEach(var -> {
            try {
                var.write(writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
