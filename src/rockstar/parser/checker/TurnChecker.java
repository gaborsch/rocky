/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.statement.Statement;
import rockstar.statement.TurnStatement;
import rockstar.statement.TurnStatement.TurnDirection;

/**
 *
 * @author Gabor
 */
public class TurnChecker extends Checker<VariableReference, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Turn", variableAt(1), "up"),
        new ParamList("Turn", "up", variableAt(1)),
        new ParamList("Turn", variableAt(1), "down"),
        new ParamList("Turn", "down", variableAt(1)),
        new ParamList("Turn", variableAt(1), "round"),
        new ParamList("Turn", "round", variableAt(1)),
        new ParamList("Turn", variableAt(1), "around"),
        new ParamList("Turn", "around", variableAt(1))};

    private static final TurnDirection[] DIRECTION_LOOKUP = new TurnDirection[]{
        TurnDirection.UP, TurnDirection.UP,
        TurnDirection.DOWN, TurnDirection.DOWN,
        TurnDirection.ROUND, TurnDirection.ROUND,
        TurnDirection.ROUND, TurnDirection.ROUND
    };

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        // set direction for the match count
        int matchIdx = getMatchCounter() - 1;
        TurnDirection dir = DIRECTION_LOOKUP[matchIdx];
        return new TurnStatement(varRef, dir);
    }

}
