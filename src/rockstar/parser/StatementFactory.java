/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import rockstar.parser.checker.*;
import rockstar.statement.Block;
import rockstar.statement.Statement;
import rockstar.statement.StatementError;

/**
 *
 * @author Gabor
 */
public class StatementFactory {

    private static final Checker<?, ?, ?> CHECKERS[] = new Checker[]{
        new AliasChecker(),
        new PkgDefChecker(),
        new ImportChecker(),
        new TakeItToTheTopChecker(),
        new BreakItDownChecker(),
        new ListenChecker(),
        new SayChecker(),
        new AssignmentChecker(),
        new BuildUpChecker(),
        new KnockDownChecker(),
        new FunctionDefChecker(),
        new IterateChecker(),
        new WhileChecker(),
        new UntilChecker(),
        new IfChecker(),
        new ElseChecker(),
        new GiveBackChecker(),
        new BlockEndChecker(),
        new PoeticArrayAssignmentChecker(),
        new PoeticAssignmentChecker(),
        new PoeticStringAssignmentChecker(),
        new RockPoeticChecker(),
        new RockChecker(),
        new RockChecker2(),
        new RollChecker(),
        new RollChecker2(),
        new TurnChecker(),
        new CastChecker(),
        new SplitChecker(),
        new JoinChecker(),
        new ClassBlockChecker(),
        new InstantiationChecker(),
        new ExpressionStatementChecker(),
        new RaiseError()
    };

    public static Statement getStatementFor(Line line, Block currentBlock) {
        Statement stmt = null;
        for (Checker<?, ?, ?> checker : CHECKERS) {
            stmt = checker.initialize(line, currentBlock).check();
            if (stmt != null) {
                stmt.setDebugInfo(line);
                stmt.setBlock(currentBlock);
                break;
            }
        }
        return stmt;
    }

    private static class RaiseError extends Checker<Object, Object, Object> {

        @Override
        public Statement check() {
            // basic implementation
            return new StatementError(0, "Invalid statement");
        }
    }

}
