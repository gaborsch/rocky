/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;

import rockstar.runtime.ASTAware;

/**
 *
 * @author Gabor
 */
public abstract class SimpleExpression extends Expression {

    @Override
    public List<ASTAware> getASTChildren() {
        return null;
    }

}
