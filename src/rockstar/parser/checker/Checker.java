/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.LinkedList;
import java.util.List;
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

    private boolean hasMatch = false;
    private int matchCounter = 0;
    private Object[] matchedParams;

    public List<String>[] getResult() {
        return result;
    }

    public int getMatchCounter() {
        return matchCounter;
    }
    
    public Checker initialize(Line l, Block currentBlock) {
        this.line = l;
        this.block = currentBlock;
        this.hasMatch = false;
        this.matchedParams = null;
        this.matchCounter = 0;
        return this;
    }

    public abstract Statement check();

    /**
     * Matches a statement pattern, e.g. [1, "this", 3, "that" "other" 2]
     * Numbers represent placeholders, result[n] will be set to the matched sub-list
     * Strings represent string tokens
     * @param params
     * @return 
     */
    public boolean match(Object... params) {
        // do not overwrite existing result
        if (this.hasMatch) {
            return false;
        }
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
                    needle = new LinkedList();
                    needle.add(((String) param).toLowerCase());
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
        this.hasMatch = true;
        this.matchedParams = params;
        return true;
    }

    private void findNext(List<String> needle, int lastPos, List<String> tokens) {
        List<List<String>> allNeedles = block.getAliasesFor(needle);
        
        int tokenLen = tokens.size();
        
        for (int idx = lastPos + 1; idx < tokenLen; idx++) {
            for (List<String> currentNeedle : allNeedles) {
                boolean matching = true;
                int len = currentNeedle.size();
                for(int i = 0; i<len && idx+i < tokenLen; i++) {
                    String token = tokens.get(idx + i);
                    if (! token.equalsIgnoreCase(currentNeedle.get(i))) {
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
    
    protected String getMatchedStringObject(int n) {
        int cnt = 0;
        for (Object param : matchedParams) {
            if(param instanceof String) {
                cnt++;
                if (cnt == n) {
                    return (String) param;
                }
            }
        }
        return null;
    }
    
}
