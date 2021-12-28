/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.NativeObject;
import rockstar.runtime.QualifiedClassName;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InstantiationStatement extends Statement {

    private final VariableReference variable;
    private final VariableReference classRef;
    private final List<Expression> ctorParameterExprs = new ArrayList<>();

    public InstantiationStatement(VariableReference variable, VariableReference classRef) {
        this.variable = variable;
        this.classRef = classRef;
    }

    public void addParameter(Expression expr) {
        ctorParameterExprs.add(expr);
    }

    @Override
    public void execute(BlockContext ctx) {
    	Value instance = null;

        // evaluate constructor expressions
        List<Value> paramValues = ctorParameterExprs.stream()
                .map(expr -> expr.evaluate(ctx))
                .collect(Collectors.toList());

        // Check if native class defined
    	Value nativeClassValue = ctx.getFileCtx().getVariableValue(classRef);
    	if (nativeClassValue != null && nativeClassValue.isNative()) {
    		// get the class representation
    		NativeObject nativeClass = nativeClassValue.getNative();
    		// instantiate
    		instance = Value.getValue(nativeClass.newInstance(paramValues));
    	}
    	if (instance == null) {    	
	        // check if Rockstar class is defined
	        QualifiedClassName qcn = ctx.findClass(classRef.getName());
	        ClassBlock classBlock = ctx.getRootCtx().retrieveClass(qcn);
	        if (classBlock != null) {
	            // instantiate the class
	            instance = classBlock.instantiate(paramValues);
	        }
    	}
        
        if (instance != null) {
            // assign the instance to the variable
            ctx.setVariable(this.variable, instance);
        } else {
        	throw new RockstarRuntimeException("Undefined class: " + classRef.geTokensAsString());
        }
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + " of class " + classRef.geTokensAsString();
    }

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astParams = new LinkedList<>();
        astParams.add(variable);
        astParams.addAll(ctorParameterExprs);
        return astParams;
    }

}
