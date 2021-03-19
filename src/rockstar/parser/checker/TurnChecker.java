/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.HashMap;
import java.util.Map;
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

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Turn", 1, "up"),
        new ParamList("Turn", "up", 1),
        new ParamList("Turn", 1, "down"),
        new ParamList("Turn", "down", 1),
        new ParamList("Turn", 1, "round"),
        new ParamList("Turn", "round", 1),
        new ParamList("Turn", 1, "around"),
        new ParamList("Turn", "around", 1)};

    private static final Direction[] DIRECTION_LOOKUP = new Direction[]{
        Direction.UP, Direction.UP,
        Direction.DOWN, Direction.DOWN,
        Direction.ROUND, Direction.ROUND,
        Direction.ROUND, Direction.ROUND
    };

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (varRef != null) {
            // set direction for the match count
            int matchIdx = getMatchCounter() - 1;
            Direction dir = DIRECTION_LOOKUP[matchIdx];
            return new TurnStatement(varRef, dir);
        }
        return null;
    }

}
