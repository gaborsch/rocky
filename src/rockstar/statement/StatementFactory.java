/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.ExpressionFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rockstar.expression.ConstantValue;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.Line;

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
        new WhileChecker(),
        new IfChecker(),
        new ElseChecker(),
        new GiveBackChecker(),
        new BlockEndChecker(),
        new PoeticAssignmentChecker(),
        new ExpressionStatementChecker(),
        new NoOpChecker()
    };

    public static Statement getStatementFor(Line l) {
        Statement stmt = null;
        for (Checker checker : CHECKERS) {
            stmt = checker.initialize(l).check();
            if (stmt != null) {
                break;
            }
        }

        if (stmt != null) {
            stmt.setDebugInfo(l);
        }

        return stmt;
    }

    private static abstract class Checker {

        protected Line line;
//        private final Map<String, Integer> positionsMap = new HashMap<>();

        private final List<String>[] result = new List[10];
        private boolean hasMatch = false;

        public List<String>[] getResult() {
            return result;
        }

        public Checker initialize(Line l) {
            this.line = l;
//            positionsMap.clear();
//            int i = 0;
//            for (String token : l.getTokens()) {
//                positionsMap.putIfAbsent(token, i++);
//            }
            this.hasMatch = false;
            return this;
        }

        abstract Statement check();

        // 1, "this", 3, "that" "other" 2
        boolean match(Object... params) {
            // do not overwrite existing result
            if (this.hasMatch) {
                return false;
            }
            List<String> tokens = line.getTokens();
            // clear previous result
            for (int i = 0; i < result.length; i++) {
                result[i] = null;
            }
            // match cycle
            int lastPos = -1;
            Integer lastNum = null;
            for (Object param : params) {
                if (param instanceof String) {
                    int nextPos = this.findNext((String) param, lastPos);
                    if (nextPos > lastPos) {
                        if (lastNum != null) {
                            // save the sublist as the numbered result
                            result[lastNum] = tokens.subList(lastPos + 1, nextPos);
                            lastNum = null;
                        } else if (nextPos != lastPos + 1) {
                            // tokens must follow each other
                            return false;
                        }
                        lastPos = nextPos;
                    } else {
                        // wrong order
                        return false;
                    }
                } else if (param instanceof Integer) {
                    lastNum = (Integer) param;
                }
            }
            if (lastNum != null) {
                // save the tail as the numbered result
                result[lastNum] = tokens.subList(lastPos + 1, tokens.size());
            } else if (lastPos + 1 < tokens.size()) {
                // if there are tokens after the last
                return false;
            }
            this.hasMatch = true;
            return true;
        }

        private int findNext(String needle, int lastPos) {
            List<String> tokens = line.getTokens();
            for (int idx = lastPos + 1; idx < tokens.size(); idx++) {
                String token = tokens.get(idx);
                if (token.equals(needle)) {
                    return idx;
                } else if (idx == 0
                        && Character.toUpperCase(token.charAt(0)) == needle.charAt(0)
                        && token.substring(1).equals(needle.substring(1))) {
                    // first token, first character may be lowercase
                    return idx;
                }

            }
            return -1;
        }
    }

    private static class ListenChecker extends Checker {

        @Override
        Statement check() {
            if (match("Listen", "to", 1)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[1]);
                if (varRef != null) {
                    return new InputStatement(varRef);
                }
            }
            this.initialize(line);
            if (match("Listen", 1)) {
                if (getResult()[1].isEmpty()) {
                    return new InputStatement();
                }
            }
            return null;
        }
    }

    private static class SayChecker extends Checker {

        @Override
        Statement check() {
            if (match("Say", 1)
                    || match("Shout", 1)
                    || match("Whisper", 1)
                    || match("Scream", 1)) {
                Expression expr = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (expr != null) {
                    return new OutputStatement(expr);
                }
            }
            return null;
        }
    }

    private static class FunctionDefChecker extends Checker {

        @Override
        Statement check() {
            int paramCount = -1;
            if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8, "and", 9)) {
                paramCount = 9;
            } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8)) {
                paramCount = 8;
            } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7)) {
                paramCount = 7;
            } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6)) {
                paramCount = 6;
            } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5)) {
                paramCount = 5;
            } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4)) {
                paramCount = 4;
            } else if (match(0, "takes", 1, "and", 2, "and", 3)) {
                paramCount = 3;
            } else if (match(0, "takes", 1, "and", 2)) {
                paramCount = 2;
            } else if (match(0, "takes", 1)) {
                paramCount = 1;
            } else if (match(0, "takes", "nothing")) {
                paramCount = 0;
            }
            if (paramCount >= 0) {
                // function name is the same as a variable name
                VariableReference nameRef = ExpressionFactory.getVariableReferenceFor(getResult()[0]);
                if (nameRef != null) {
                    FunctionBlock fb = new FunctionBlock(nameRef.getName());

                    VariableReference paramRef;
                    for (int i = 1; i <= paramCount; i++) {
                        paramRef = ExpressionFactory.getVariableReferenceFor(getResult()[i]);
                        if (paramRef != null) {
                            fb.addParameterName(paramRef.getName());
                        } else {
                            return null;
                        }
                    }
                    return fb;
                }
            }
            return null;
        }

    }

    private static class WhileChecker extends Checker {

        @Override
        Statement check() {
            if (match("While", 1)) {
                Expression condition = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (condition != null) {
                    return new WhileStatement(condition);
                }
            }
            this.initialize(line);
            if (match("Until", 1)) {
                Expression condition = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (condition != null) {
                    return new WhileStatement(condition, true);
                }
            }
            return null;
        }
    }

    private static class IfChecker extends Checker {

        @Override
        Statement check() {
            if (match("If", 1)
                    || match("When", 1)) {
                Expression condition = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (condition != null) {
                    return new IfStatement(condition);
                }
            }
            return null;
        }
    }

    private static class ElseChecker extends Checker {

        @Override
        Statement check() {
            if (match("Else", 1)
                    || match("Otherwise", 1)) {
                return new ElseStatement();
            }
            return null;
        }
    }

    private static class GiveBackChecker extends Checker {

        @Override
        Statement check() {
            if (match("Give", "back", 1)) {
                Expression expression = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (expression != null) {
                    return new ReturnStatement(expression);
                }
            }
            return null;
        }

    }

    private static class BlockEndChecker extends Checker {

        @Override
        Statement check() {
            if (line.getTokens().isEmpty()) {
                return new BlockEnd();
            }
            return null;
        }
    }

    private static class ExpressionStatementChecker extends Checker {

        @Override
        Statement check() {
            if (match(1)) {
                try {
                    Expression expression = ExpressionFactory.getExpressionFor(getResult()[1]);
                    if (expression != null) {
                        return new ExpressionStatement(expression);
                    }
                } catch (Exception e) {
                    // if expession is not parsed properly, we must continue
                }
            }
            return null;
        }

    }

    private static class AssignmentChecker extends Checker {

        @Override
        Statement check() {
            if (match("Put", 1, "into", 2)
                    || match("Let", 2, "be", 1)
                    || match(2, "thinks", 1)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[2]);
                Expression expr = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (varRef != null && expr != null) {
                    return new AssignmentStatement(varRef, expr);
                }
            }
            return null;
        }
    }

    private static class PoeticAssignmentChecker extends Checker {

        @Override
        Statement check() {
            if (match(1, "is", 2)
                    || match(1, "was", 2)
                    || match(1, "are", 2)
                    || match(1, "were", 2)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[1]);
                ConstantValue value = ExpressionFactory.getPoeticLiteralFor(getResult()[2]);
                if (varRef != null && value != null) {
                    return new AssignmentStatement(varRef, value);
                }
            }
            this.initialize(line);
            if (match(1, "says", 2)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[1]);

                if (varRef != null) {
                    // grab original string from line
                    String poeticLiteralString = line.getOrigLine().substring(line.getOrigLine().indexOf("says ") + 5);
                    ConstantValue value = new ConstantValue(poeticLiteralString);
                    return new AssignmentStatement(varRef, value);
                }
            }
            return null;
        }
    }

    private static class BuildUpChecker extends Checker {

        @Override
        Statement check() {
            if (match("Build", 1, "up", 2)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[1]);
                int count = 1;
                boolean isAndPossible = true;
                for (String s : getResult()[2]) {
                    if ("up".equals(s)) {
                        count++;
                        isAndPossible = true;
                    } else if (isAndPossible && s.equals("and")) {
                        isAndPossible = false;
                    } else {
                        return null;
                    }
                }
                if (varRef != null) {
                    return new IncrementStatement(varRef, count);
                }
            }
            return null;
        }
    }

    private static class KnockDownChecker extends Checker {

        @Override
        Statement check() {
            if (match("Knock", 1, "down", 2)) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[1]);
                int count = 1;
                boolean isAndPossible = true;
                for (String s : getResult()[2]) {
                    if ("down".equals(s)) {
                        count++;
                        isAndPossible = true;
                    } else if (isAndPossible && s.equals("and")) {
                        isAndPossible = false;
                    } else {
                        return null;
                    }
                }
                if (varRef != null) {
                    return new DecrementStatement(varRef, count);
                }
            }
            return null;
        }
    }

    private static class TakeItToTheTopChecker extends Checker {

        @Override
        Statement check() {
            if (match("Take", "it", "to", "the", "top") || match("Continue")) {
                return new ContinueStatement();
            }
            return null;
        }
    }

    private static class BreakItDownChecker extends Checker {

        @Override
        Statement check() {
            if (match("Break", "it", "down") || match("Break")) {
                return new BreakStatement();
            }
            return null;
        }
    }

    private static class NoOpChecker extends Checker {

        @Override
        Statement check() {
            throw new RuntimeException("NoOp: " + line.getLine());
//            return new NoOpStatement();
        }
    }

}
