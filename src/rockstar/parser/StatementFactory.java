/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import rockstar.parser.checker.IfChecker;
import rockstar.parser.checker.ListenChecker;
import rockstar.parser.checker.BuildUpChecker;
import rockstar.parser.checker.BlockEndChecker;
import rockstar.parser.checker.AssignmentChecker;
import rockstar.parser.checker.UntilChecker;
import rockstar.parser.checker.GiveBackChecker;
import rockstar.parser.checker.SayChecker;
import rockstar.parser.checker.PoeticAssignmentChecker;
import rockstar.parser.checker.PoeticStringAssignmentChecker;
import rockstar.parser.checker.IterateChecker;
import rockstar.parser.checker.WhileChecker;
import rockstar.parser.checker.Checker;
import rockstar.parser.checker.ExpressionStatementChecker;
import rockstar.parser.checker.BreakItDownChecker;
import rockstar.parser.checker.ClassBlockChecker;
import rockstar.parser.checker.KnockDownChecker;
import rockstar.parser.checker.ElseChecker;
import rockstar.parser.checker.FunctionDefChecker;
import rockstar.parser.checker.InstantiationChecker;
import rockstar.parser.checker.PkgDefChecker;
import rockstar.parser.checker.PoeticArrayAssignmentChecker;
import rockstar.parser.checker.TakeItToTheTopChecker;
import rockstar.parser.checker.PushChecker;
import rockstar.parser.checker.PullChecker;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class StatementFactory {

    private static final Checker CHECKERS[] = new Checker[]{
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
        new PushChecker(),
        new PullChecker(),
        new ClassBlockChecker(),
        new InstantiationChecker(),
        new ExpressionStatementChecker(),
        new PkgDefChecker(),
        new NoOpChecker()
    };

    public static Statement getStatementFor(Line line) {
        Statement stmt = null;
        for (Checker checker : CHECKERS) {
            stmt = checker.initialize(line).check();
            if (stmt != null) {
                break;
            }
        }

        if (stmt != null) {
            stmt.setDebugInfo(line);
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
