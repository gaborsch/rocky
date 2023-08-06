/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class TurnStatement extends Statement {

    public enum TurnDirection {
        UP("ceil"),
        DOWN("floor"),
        ROUND("round");

        String desc;

        private TurnDirection(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    };

    final VariableReference variable;
    final TurnDirection direction;

    public TurnStatement(VariableReference variable, TurnDirection direction) {
        this.variable = variable;
        this.direction = direction;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = ctx.getVariableValue(variable);
        if (v.isMysterious() || v.isNull()) {
            v = Value.getValue(RockNumber.ZERO());
            // v is set to a numeric value
            ctx.setVariable(variable, v);
        }
        if (v.isNumeric()) {
            // round it according to the direction
            RockNumber num = v.getNumeric();
            switch (direction) {
                case UP:
                    num = num.ceil();
                    break;
                case DOWN:
                    num = num.floor();
                    break;
                case ROUND:
                    num = num.round();
                    break;
            }
            ctx.setVariable(variable, Value.getValue(num));
            return;
        }
        throw new RockstarRuntimeException("turn " + v.getType() + " " + direction);
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + " " + direction;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(variable);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
