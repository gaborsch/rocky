/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import static rockstar.parser.checker.Checker.PlaceholderType.MUTATION_EXPRESSION;
import static rockstar.parser.checker.Checker.PlaceholderType.POETIC_LITERAL;
import static rockstar.parser.checker.Checker.PlaceholderType.TEXT;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.parser.Line;
import rockstar.parser.Token;
import rockstar.statement.Block;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
public abstract class Checker<T1, T2, T3> {

    protected Line line;
    protected Block block;

    private final Object[] parsedResult = new Object[4];
    private int lastPos;
    private Placeholder lastPH;
    private int nextPosStart;
    private int nextPosEnd;

    private int matchCounter = 0;

    @SuppressWarnings("unchecked")
    public T1 getE1() {
        return (T1) parsedResult[1];
    }

    @SuppressWarnings("unchecked")
    public T2 getE2() {
        return (T2) parsedResult[2];
    }

    @SuppressWarnings("unchecked")
    public T3 getE3() {
        return (T3) parsedResult[3];
    }

    public int getMatchCounter() {
        return matchCounter;
    }

    public Checker<T1, T2, T3> initialize(Line l, Block currentBlock) {
        this.line = l;
        this.block = currentBlock;
        this.matchCounter = 0;
        return this;
    }

    public abstract Statement check();

    /**
     * Matches a statement pattern, e.g. [1, ["this"], 3, ["that" "other"], 2]
     * Numbers represent placeholders, parsedResult[n] will be set to the matched
     * sub-list Strings represent string tokens
     *
     * @param params
     * @return
     */
    private boolean match(Object... params) {
        matchCounter++;
        List<Token> tokenList = line.getTokens();
        // clear previous result
        for (int i = 0; i < parsedResult.length; i++) {
            parsedResult[i] = null;
        }
        // match cycle
        lastPos = -1;
        lastPH = null;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Placeholder) {
                lastPH = ((Placeholder) param);
            } else {
                List<String> needle = null;
                if (param instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> needle1 = (List<String>) param;
                    needle = needle1;
                } else if (param instanceof String) {
                    needle = Arrays.asList((String) param);
                }
                // set nextPosStart and nextPosEnd
                findNext(needle, lastPos, tokenList);

                if (nextPosEnd > lastPos) {
                    if (lastPH != null) {
                        // save the sublist as the numbered result
                        boolean success = saveResultPosition(lastPH, lastPos+1, nextPosStart);
                        if (!success) {
                            return false;
                        }
                        lastPH = null;
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
        if (lastPH != null) {
            // save the tail as the numbered result
            boolean success = saveResultPosition(lastPH, lastPos+1, tokenList.size());
            if (!success) {
                return false;
            }
        } else if (lastPos + 1 < tokenList.size()) {
            // if there are tokens after the last
            return false;
        }
        return true;
    }

    private boolean saveResultPosition(Placeholder ph, int start, int end) {
        // if there are no tokens
        if (start == end) {
            // we only accept if it is optional
            return ph.isOptional();
        }
        List<Token> tokenList = line.getTokens().subList(start, end);

        boolean validExpr = false;
        boolean validText = false;
        Expression e = null;
        if (ph.getType() == POETIC_LITERAL) {
            String origTail = line.getOrigLine().substring(line.getTokens().get(lastPos+1).getPos());
            e = ExpressionFactory.getPoeticLiteralFor(tokenList, line, origTail, block);
            validExpr = true;
        } else {
            // default expression may be defined - hopefully it has already been parsed
            Expression defaultExpr = (ph.getDefaultExprPos() == null) ? null : (Expression) parsedResult[ph.getDefaultExprPos()];

            
            if (ph.getType() == MUTATION_EXPRESSION) {
                e = ExpressionFactory.tryMutationExpressionFor(tokenList, line, block);
            } else if (ph.getType() != TEXT) {
                e = ExpressionFactory.tryExpressionFor(tokenList, line, defaultExpr, block);
            }

            switch (ph.getType()) {
                case EXPRESSION:
                case MUTATION_EXPRESSION:
                    validExpr = (e != null);
                    break;
                case VARIABLE_OR_QUALIFIER:
                    validExpr = (e != null) && ((e instanceof VariableReference) || (e instanceof QualifierExpression));
                    break;
                case VARIABLE:
                    validExpr = (e != null) && (e instanceof VariableReference);
                    break;
                case LITERAL:
                    validExpr = (e != null) && (e instanceof ConstantExpression);
                    break;
                case LITERAL_OR_VARIABLE:
                    validExpr = (e != null) && ((e instanceof ConstantExpression) || (e instanceof VariableReference));
                    break;
                case VARIABLE_OR_LIST:
                    validExpr = (e != null) && ((e instanceof VariableReference) || (e instanceof ListExpression));
                    break;
                case TEXT:
                    validText = true;
                    break;
                default:
                    throw new RuntimeException("Unhandled expression type:" + ph.getType());
            }
        }
        if (validExpr && e != null) {
            parsedResult[ph.getPosition()] = e;
        } else if (validText) {
            parsedResult[ph.getPosition()] = tokenList;
        }
        return validExpr || validText;
            
    }

    private void findNext(List<String> needle, int lastPos, List<Token> tokenList) {
        List<List<String>> allNeedles = block.getAliasesFor(needle);

        int tokenLen = tokenList.size();

        for (int idx = lastPos + 1; idx < tokenLen; idx++) {
            for (List<String> currentNeedle : allNeedles) {
                boolean matching = true;
                int len = currentNeedle.size();
                for (int i = 0; i < len && idx + i < tokenLen; i++) {
                    String token = tokenList.get(idx + i).getValue();
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

    public static Placeholder textAt(int pos) {
        return new Placeholder(PlaceholderType.TEXT, pos);
    }

    public static Placeholder literalAt(int pos) {
        return new Placeholder(PlaceholderType.LITERAL, pos);
    }

    public static Placeholder variableAt(int pos) {
        return new Placeholder(PlaceholderType.VARIABLE, pos);
    }

    public static Placeholder expressionAt(int pos) {
        return new Placeholder(PlaceholderType.EXPRESSION, pos);
    }

    public static Placeholder mutationExpressionAt(int pos) {
        return new Placeholder(PlaceholderType.MUTATION_EXPRESSION, pos);
    }

    public static Placeholder poeticLiteralAt(int pos) {
        return new Placeholder(PlaceholderType.POETIC_LITERAL, pos);
    }

//    public static Placeholder at(int pos, PlaceholderType type) {
//        return new Placeholder(type, pos);
//    }

    public enum PlaceholderType {
        TEXT,
        LITERAL,
        VARIABLE,
        LITERAL_OR_VARIABLE,
        VARIABLE_OR_QUALIFIER,        
        VARIABLE_OR_LIST,
        EXPRESSION,
        MUTATION_EXPRESSION,
        POETIC_LITERAL;
        
        Placeholder at(int pos) {
            return new Placeholder(this, pos);
        }
    }

    public static class Placeholder {

        private final PlaceholderType type;
        private final int position;

        private boolean optional;
        private Integer defaultExprPos;

        public Placeholder(PlaceholderType type, int position) {
            this.type = type;
            this.position = position;
        }

        public Placeholder opt() {
            this.optional = true;
            return this;
        }

        public Placeholder withDefaultExprAt(int pos) {
            this.defaultExprPos = pos;
            return this;
        }

        public PlaceholderType getType() {
            return type;
        }

        public int getPosition() {
            return position;
        }

        public boolean isOptional() {
            return optional;
        }

        public Integer getDefaultExprPos() {
            return defaultExprPos;
        }

		@Override
		public String toString() {
			return "Placeholder "
					+ type 
					+" @" 
					+ position 
					+ " [" 
					+ (optional ? " optional" : "")
					+ (defaultExprPos!=null ? ("defaultExprPos=" + defaultExprPos + " " ) : "") 
					+ "]";
		}
        

        
        
    }

}
