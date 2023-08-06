/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarReturnException;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class FunctionBlock extends Block {

    private final String name;
    private final List<VariableReference> parameterRefs = new ArrayList<>();

    public FunctionBlock(String name) {
        this.name = name;
    }

    public void addParameterName(VariableReference paramRef) {
        parameterRefs.add(paramRef);
    }

    public String getName() {
        return name;
    }

    public List<VariableReference> getParameterRefs() {
        return parameterRefs;
    }

    public boolean isAbstract() {
        return getStatements().isEmpty();
    }

    /**
     * Define a function
     *
     * @param ctx
     */
    @Override
    public void execute(BlockContext ctx) {
        // define function
        ctx.defineFunction(name, this);
    }

    /**
     * Execute a function call
     *
     * @param ctx Context for execution
     * @param values function parameters
     * @return
     */
    public Value call(BlockContext ctx, List<Value> values) {
        BlockContext funcCtx = new BlockContext(ctx, name);
        List<VariableReference> refs = this.parameterRefs;
        if (refs.size() != values.size()) {
            throw new RockstarRuntimeException("Wrong number of arguments for function " + this.name + ": expected " + refs.size() + ", got " + values.size());
        }

        for (int i = 0; i < values.size(); i++) {
            funcCtx.setLocalVariable(refs.get(i), values.get(i));
            funcCtx.afterExpression(refs.get(i), values.get(i));
        }

        try {
            // execute the function body
            super.execute(funcCtx);
        } catch (RockstarReturnException retExp) {
            // return value is set by the return statement
            return retExp.getReturnValue();
        }
        // no explicite return value was set
        return Value.MYSTERIOUS;
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + " " + name;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astChildren = new ArrayList<>();
        astChildren.addAll(parameterRefs);
        astChildren.addAll(super.getASTChildren());
        return astChildren;
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
