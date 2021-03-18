/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import rockstar.parser.checker.*;
import rockstar.statement.Block;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class StatementFactory {

    private static final Checker CHECKERS[] = new Checker[]{
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
        new RollChecker(),
        new TurnChecker(),
        new CastChecker3(),
        new SplitChecker(),
        new JoinChecker(),
        new ClassBlockChecker(),
        new InstantiationChecker(),
        new ExpressionStatementChecker(),
        new NoOpChecker()
    };

    public static Statement getStatementFor(Line line, Block currentBlock) {
        Statement stmt = null;
        for (Checker checker : CHECKERS) {
            stmt = checker.initialize(line, currentBlock).check();
            if (stmt != null) {
                break;
            }
        }
        
        if (stmt != null) {
            stmt.setDebugInfo(line);
            stmt.setBlock(currentBlock);
        }

        return stmt;
    }

    private static class NoOpChecker extends Checker {

        @Override
        public Statement check() {
            throw new ParseException("Invalid statement: " + line.getLine(), line);
        }
    }

}
