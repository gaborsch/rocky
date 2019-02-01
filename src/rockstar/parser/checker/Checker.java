/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.parser.Line;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public abstract class Checker {
    
    protected Line line;
    //        private final Map<String, Integer> positionsMap = new HashMap<>();
    private final List<String>[] result = new List[10];
    private boolean hasMatch = false;

    public List<String>[] getResult() {
        return result;
    }

    public Checker initialize(Line l) {
        this.line = l;
        this.hasMatch = false;
        return this;
    }

    public abstract Statement check();

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
            if (token.length() != needle.length()) {
                continue;
            }
            if (token.equals(needle)) {
                return idx;
            } else if ( /*idx == 0
            &&*/ Character.toUpperCase(token.charAt(0)) == Character.toUpperCase(needle.charAt(0)) && token.substring(1).equals(needle.substring(1))) {
                // first token, first character may be lowercase
                return idx;
            }
        }
        return -1;
    }
    
}
