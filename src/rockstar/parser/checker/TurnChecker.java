/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.Statement;
import rockstar.statement.TurnStatement;
import rockstar.statement.TurnStatement.Direction;

/**
 *
 * @author Gabor
 */
public class TurnChecker extends Checker {

    private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

    private static final Direction[] DIRECTION_LOOKUP = new Direction[]{
        Direction.UP, Direction.UP,
        Direction.DOWN, Direction.DOWN,
        Direction.ROUND, Direction.ROUND,
        Direction.ROUND, Direction.ROUND
    };

    @Override
    public Statement check() {
        if (match("Turn", 1, "up") || match("Turn", "up", 1)
                || match("Turn", 1, "down") || match("Turn", "down", 1)
                || match("Turn", 1, "round") || match("Turn", "round", 1)
                || match("Turn", 1, "around") || match("Turn", "around", 1)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line, block);
            if (varRef != null) {
                // set direction for the match count
                int matchIdx = getMatchCounter() - 1;
                Direction dir = DIRECTION_LOOKUP[matchIdx];

                return new TurnStatement(varRef, dir);
            }
        }
        return null;
    }

}
