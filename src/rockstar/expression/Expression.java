/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public abstract class Expression implements ASTAware {

    public abstract Value evaluate(BlockContext ctx);

    public abstract String format();

    @Override
    public String getASTNodeText() {
        return this.getClass().getSimpleName().replace("Expression", "") + " " + format();
    }

    @Override
    public Integer getASTLineNum() {
        return null;
    }

}
