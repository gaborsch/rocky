/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ListExpression extends CompoundExpression {

    private boolean hasVariableRef = false;
    private boolean hasConstant = false;
    private boolean hasCompound = false;

    public ListExpression(Expression... params) {
        super();
        for (Expression param : params) {
            addParameter(param);
        }
    }

    public boolean hasCompound() {
        return hasCompound;
    }

    public boolean hasConstant() {
        return hasConstant;
    }

    public boolean hasVariableRef() {
        return hasVariableRef;
    }
    
    

    @Override
    public Value evaluate(BlockContext ctx) {
        throw new RockstarRuntimeException("Cannot evaluate a list of expressions");
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        getParameters().forEach(expr -> sb.append(sb.length() == 0 ? "" : ", ").append(expr.format()));
        return "("+sb.toString()+")";
    }

    @Override
    public int getPrecedence() {
        return 80;
    }

    @Override
    public int getParameterCount() {
        // list always consumers 2 elements, it will meld later
        return 2;
    }

    @Override
    public String getFormat() {
        StringBuilder sb = new StringBuilder();
        getParameters().forEach(expr -> sb.append(sb.length() == 0 ? "%s" : ", %s"));
        return "(" + sb.toString() + ")";
    }

    @Override
    public final void addParameter(Expression parameter) {
        super.addParameter(parameter);
        checkParameter(parameter);
    }

    @Override
    public void addParameterReverse(Expression parameter) {
        super.addParameterReverse(parameter);
        checkParameter(parameter);
    }

    private void checkParameter(Expression parameter) {
        if (parameter instanceof VariableReference) {
            hasVariableRef = true;
        } else if (parameter instanceof ConstantExpression) {
            hasConstant = true;
        } else if (parameter instanceof CompoundExpression) {
            hasCompound = true;
        }
    }

    @Override
    public CompoundExpression setupFinished() {
        if (hasCompound) {
            ListExpression newExpr = expandTo(this, new ListExpression());
            return newExpr;
        }
        return this;
    }

    public static ListExpression asListExpression(Expression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof ListExpression) {
            return (ListExpression) expr;
        }
        if (expr instanceof ConstantExpression) {
            return new ListExpression(expr);
        }
        if (expr instanceof VariableReference && !((VariableReference) expr).isFunctionName()) {
            return new ListExpression(expr);
        }
        if (expr instanceof LogicalExpression) {
            LogicalExpression lexpr = (LogicalExpression) expr;
            if (lexpr.getType() == LogicalExpression.LogicalType.AND) {
                ListExpression newExpr = expandTo(lexpr, new ListExpression());
                return newExpr;
            }
        }
        return null;
    }

    /**
     * Expands all compound expressions into a list,
     *
     * @param expr
     * @param newExpr
     */
    private static ListExpression expandTo(Expression expr, ListExpression newExpr) {
        if (expr instanceof ListExpression) {
            CompoundExpression compExpr = (CompoundExpression) expr;
            compExpr.getParameters().forEach(expr2 -> expandTo(expr2, newExpr));
        } else if (expr instanceof LogicalExpression) {
            LogicalExpression lexpr = (LogicalExpression) expr;
            if (lexpr.getType() == LogicalExpression.LogicalType.AND) {
                lexpr.getParameters().forEach(expr2 -> expandTo(expr2, newExpr));
            }
        } else if (expr instanceof SimpleExpression) {
            newExpr.addParameter(expr);
        } else {
            return null;
        }
        return newExpr;
    }

}
