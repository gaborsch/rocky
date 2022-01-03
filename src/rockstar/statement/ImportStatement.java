/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import rockstar.expression.VariableReference;
import rockstar.parser.Token;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.FileContext;
import rockstar.runtime.NativeObject;
import rockstar.runtime.PackagePath;
import rockstar.runtime.ProgramContext;
import rockstar.runtime.QualifiedClassName;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ImportStatement extends Statement {

    private final PackagePath path;
    private final List<String> names;
    private final List<List<Token>> tokens;

    public ImportStatement(PackagePath path, List<String> aliases, List<List<Token>> names) {
        this.path = path;
        this.names = aliases;
        this.tokens = new ArrayList<>(names);
    }

    @Override
    public void execute(BlockContext ctx) {
        FileContext fileCtx = ctx.getFileCtx();
        ProgramContext root = ctx.getRootCtx();
        // the path is either given, or comes from the file, or the default package
        PackagePath p = this.path;
        p = (p == null) ? fileCtx.getPackagePath() : p;
        p = (p == null) ? PackagePath.DEFAULT : p;

        for (int i = 0; i < names.size(); i++) {
        	List<Token> nameTokens = tokens.get(i);
        	String alias = names.get(i++);
        	String name = nameTokens.stream().map(Token::getValue).collect(Collectors.joining(" "));
            QualifiedClassName qcn = new QualifiedClassName(p, name);

            NativeObject staticInstance = NativeObject.getStatic(qcn);
            if (staticInstance != null) {
            	// native class
            	VariableReference varRef = VariableReference.getInstance(alias);
            	root.setLocalVariable(varRef, Value.getValue(staticInstance));
            } else {
            	// Rockstar class
        		fileCtx.defineImport(alias, qcn);
        		root.importClass(qcn);
            }            
        }
    }



    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astValues = ASTValues.of(path.toString());
        astValues.addAll(ASTValues.of((String[]) names.toArray(new String[names.size()])));
        return astValues;
    }

}
