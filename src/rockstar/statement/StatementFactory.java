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
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.Line;

/**
 *
 * @author Gabor
 */
public class StatementFactory {
    
    private static final Checker CHECKERS[] = new Checker[]{ 
        new AssignmentChecker(),
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

        if(stmt != null) {
            stmt.setDebugInfo(l);
        }
        
        return stmt;
    }

    private static abstract class Checker {
        
        private Line l;
        private final Map<String,Integer> positionsMap = new HashMap<>();
        
        private final List<String>[] result = new List[10];
        private boolean hasMatch = false;

        public List<String>[] getResult() {
            return result;
        }
                
        public Checker initialize(Line l) {
            this.l = l;
            positionsMap.clear();
            int i=0;
            for (String token : l.getTokens()) {
                positionsMap.putIfAbsent(token, i++);
            }
            this.hasMatch = false;
            return this;
        }
                
        abstract Statement check();
        
        // 1, "this", 3, "that" "other" 2
        boolean match(Object ... params) {
            // do not overwrite existing result
            if(this.hasMatch) {
                return false;
            }
            List<String> tokens = l.getTokens();
            // clear previous result
            for (int i = 0; i < result.length; i++) { result[i] = null; }
            // match cycle
            int lastPos = -1;
            Integer lastNum = null;
            for (Object param : params) {
                if(param instanceof String) {
                    Integer nextPos = positionsMap.get(param);
                    if (nextPos != null && nextPos > lastPos) {
                        if (lastNum != null) {
                            // save the sublist as the numbered result
                            result[lastNum] = tokens.subList(lastPos+1, nextPos);
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
                result[lastNum] = tokens.subList(lastPos+1, tokens.size());
            }
            this.hasMatch = true;
            return true;
        };
        
    }

    private static class AssignmentChecker extends Checker {

        @Override
        Statement check() {
            if (    match("Put", 1, "into", 2) ||
                    match("put", 1, "into", 2) ) {
                VariableReference varRef = ExpressionFactory.getVariableReferenceFor(getResult()[2]);
                Expression expr = ExpressionFactory.getExpressionFor(getResult()[1]);
                if (varRef != null && expr != null) {
                    return new AssignmentStatement(varRef, expr);
                }
            }
            return null;
        }
    }
    private static class NoOpChecker extends Checker {

        @Override
        Statement check() {
            return new NoOpStatement();
        }
    }
    
    

    
}
