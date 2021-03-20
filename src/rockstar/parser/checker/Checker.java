/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import rockstar.parser.Line;
import rockstar.statement.Block;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public abstract class Checker {

    protected Line line;
    protected Block block;

    private final List<String>[] result = new List[10];
    private int lastPos;
    private Integer lastNum;
    private int nextPosStart;
    private int nextPosEnd;

    private int matchCounter = 0;

    private final Map<String, List<String>> listCache = new HashMap<>();

//    public List<String>[] getResult() {
//        return result;
//    }

    public List<String> get1() {
        return result[1];
    }

    public List<String> get2() {
        return result[2];
    }

    public List<String> get3() {
        return result[3];
    }

    public List<String> get4() {
        return result[4];
    }

    public int getMatchCounter() {
        return matchCounter;
    }

    public Checker initialize(Line l, Block currentBlock) {
        this.line = l;
        this.block = currentBlock;
        this.matchCounter = 0;
        return this;
    }

    public abstract Statement check();

    /**
     * Matches a statement pattern, e.g. [1, "this", 3, "that" "other" 2]
     * Numbers represent placeholders, result[n] will be set to the matched
     * sub-list Strings represent string tokens
     *
     * @param params
     * @return
     */
    public boolean match(Object... params) {
        matchCounter++;
        List<String> tokens = line.getTokens();
        // clear previous result
        for (int i = 0; i < result.length; i++) {
            result[i] = null;
        }
        // match cycle
        lastPos = -1;
        lastNum = null;
        for (Object param : params) {
            if (param instanceof Integer) {
                lastNum = (Integer) param;
            } else {
                List<String> needle = null;
                if (param instanceof List) {
                    needle = (List<String>) param;
                } else if (param instanceof String) {
                    needle = listCache.computeIfAbsent((String) param, s -> Arrays.asList(s));
                }
                // set nextPosStart and nextPosEnd
                findNext(needle, lastPos, tokens);

                if (nextPosEnd > lastPos) {
                    if (lastNum != null) {
                        // save the sublist as the numbered result
                        result[lastNum] = tokens.subList(lastPos + 1, nextPosStart);
                        lastNum = null;
                    } else if (nextPosStart != lastPos + 1) {
                        // tokens must follow each other
                        return false;
                    }
                    lastPos = nextPosEnd;
                } else {
                    // wrong order
                    return false;
                }
            }
        }
        if (lastNum != null) {
            // save the tail as the numbered result
            result[lastNum] = tokens.subList(lastPos + 1, tokens.size());
        } else if (lastPos + 1 < tokens.size()) {
            // if there are tokens after the last
            return false;
        }
        return true;
    }

    private void findNext(List<String> needle, int lastPos, List<String> tokens) {
        List<List<String>> allNeedles = block.getAliasesFor(needle);

        int tokenLen = tokens.size();

        for (int idx = lastPos + 1; idx < tokenLen; idx++) {
            for (List<String> currentNeedle : allNeedles) {
                boolean matching = true;
                int len = currentNeedle.size();
                for (int i = 0; i < len && idx + i < tokenLen; i++) {
                    String token = tokens.get(idx + i);
                    if (!token.equalsIgnoreCase(currentNeedle.get(i))) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    nextPosStart = idx;
                    nextPosEnd = idx + len - 1;
                    return;
                }
            }
        }
        nextPosEnd = -1;
    }

    protected Statement check(ParamList[] possibleParams, Function<ParamList, Statement> validator) {
        Statement stmt;
        for (ParamList params : possibleParams) {
            if (match(params.getParams())) {
                stmt = validator.apply(params);
                if (stmt != null) {
                    return stmt;
                }
            }
        }
        return null;
    }
}
