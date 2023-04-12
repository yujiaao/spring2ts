package com.sdadas.spring2ts.core.typescript.def;

import com.sdadas.spring2ts.core.typescript.expression.TSWritableString;
import com.sdadas.spring2ts.core.typescript.writer.CodeWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sławomir Dadas
 */
public class TSNameSpaceDef extends TSTypeDef<TSNameSpaceDef> {

    List<TSVarDef> globalVariables = new ArrayList<>();
    private String comment;


    @Override
    public void writeNameDef(CodeWriter writer) throws IOException {

        writer.writeln();
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

    public void comment(String comment) {
        this.comment = comment;
    }


    public void writeComment(CodeWriter writer, String comment) throws IOException {
        writer.writeln("/** ");
        if(comment!=null) {
            Stream.of(comment.split("\n"))
                    .forEach(s -> {
                        try {
                            writer.writeln("* "+s);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        writer.writeln("*/");
    }
}
