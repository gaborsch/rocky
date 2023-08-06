/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import rockstar.expression.BuiltinFunction;
import rockstar.expression.ComparisonExpression;
import rockstar.expression.ComparisonExpression.ComparisonType;
import rockstar.expression.CompoundExpression.Precedence;
import rockstar.expression.CompoundExpression;
import rockstar.expression.ConstantExpression;
import rockstar.expression.DivideExpression;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionError;
import rockstar.expression.ExpressionVisitor;
import rockstar.expression.FunctionCall;
import rockstar.expression.InstanceCheckExpression;
import rockstar.expression.IntoExpression;
import rockstar.expression.ListExpression;
import rockstar.expression.LogicalExpression;
import rockstar.expression.LogicalExpression.LogicalType;
import rockstar.expression.MinusExpression;
import rockstar.expression.MultiplyExpression;
import rockstar.expression.NotExpression;
import rockstar.expression.PlusExpression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.RollExpression;
import rockstar.expression.SimpleExpression;
import rockstar.expression.SliceExpression;
import rockstar.expression.UnaryMinusExpression;
import rockstar.expression.VariableReference;
import rockstar.expression.WithExpression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.Value;
import rockstar.statement.Block;

/**
 *
 * @author Gabor
 */
public class ExpressionParser {

    // tokens of the whole expression
    private final List<Token> list;
    private final Line line;
    private final Block block;

    // next position in the list
    private int idx;

    ExpressionParser(List<Token> list, Line line, Block block) {
        this.list = list;
        this.line = line;
        this.block = block;
        idx = 0;
    }

    /**
     * checks if we have parsed all tokens
     *
     * @return
     */
    boolean isFullyParsed() {
        return idx == list.size();
    }

    private boolean containsAtLeast(int count) {
        return list.size() >= idx + count;
    }

    private boolean checkCurrent(Keyword kw) {
        return kw.matches(list.get(idx).getValue());
    }

    private boolean checkCurrent(String... aliases) {
        String value = list.get(idx).getValue();
        for (String s : aliases) {
            if (value.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private void next() {
        idx++;
    }

    private void next(int count) {
        idx += count;
    }

    private boolean checkNext(String s) {
        return list.get(idx + 1).getValue().equalsIgnoreCase(s);
    }

    private boolean checkNext(Keyword kw) {
        return kw.matches(list.get(idx + 1).getValue());
    }

    private boolean checkNext(int offset, String s) {
        return list.get(idx + offset).getValue().equalsIgnoreCase(s);
    }

    private boolean checkNext(int offset, Keyword kw) {
        return kw.matches(list.get(idx + offset).getValue());
    }

    private String peekAhead(int offset) {
        String next = list.get(idx + offset).getValue();
        return next;
    }

    /**
     * Parses a String, numeric, bool, null or mysterious literal
     *
     * @return ConstantExpression on success, null otherwise
     */
    ConstantExpression parseLiteral() {
    	int startIdx = idx;
        if (!isFullyParsed()) {
        	ConstantExpression constantExpression = null;
            String token = peekAhead(0);
            if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                // string literal> strip quotes
                next();
                String literal = token.substring(1, token.length() - 1);
                // replace escaped backslash sequences with characters
                // negative lookbehind (?<!): should not match if preceded with \\
                literal = literal.replace("(?<!\\)\\t", "\t").replace("(?<!\\)\\r", "\r").replace("(?<!\\)\\n", "\n").replace("\\\\", "\\");
                constantExpression = new ConstantExpression(literal);
            } else if (checkCurrent(Keyword.MYSTERIOUS)) {
                next();
                constantExpression = ConstantExpression.CONST_MYSTERIOUS();
            } else if (checkCurrent(Keyword.EMPTY_STRING)) {
                next();
                constantExpression = ConstantExpression.CONST_EMPTY_STRING();
            } else if (checkCurrent(Keyword.NULL)) {
                next();
                constantExpression = ConstantExpression.CONST_NULL();
            } else if (checkCurrent(Keyword.EMPTY_ARRAY)) {
                next();
                constantExpression = ConstantExpression.CONST_EMPTY_ARRAY();
            } else if (checkCurrent(Keyword.BOOLEAN_TRUE)) {
                next();
                constantExpression = ConstantExpression.CONST_TRUE();
            } else if (checkCurrent(Keyword.BOOLEAN_FALSE)) {
                next();
                constantExpression = ConstantExpression.CONST_FALSE();
            } else  {            
	            RockNumber nv = RockNumber.parse(peekAhead(0).toLowerCase());
	            if (nv != null) {
	                next();
	                constantExpression =  new ConstantExpression(nv);
	            }
            }
            if (constantExpression != null) {
            	constantExpression.withTokens(list, startIdx, idx);
            	return constantExpression;
            }
            // reserved keywords are skipped
            while (checkCurrent(Keyword.RESERVED)) {
                next();
                if (isFullyParsed()) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * parses a variable name or function name (including "it" back-reference)
     *
     * @return VariableReference on success, null otherwise
     */
    VariableReference parseVariableReference() {
        String name = null;
        int startIdx = idx;
        if (isFullyParsed()) {
            return null;
        }
        String token0 = peekAhead(0);
        String token0LC = token0.toLowerCase();
        // "my" "dream"
        if (checkCurrent(Keyword.COMMON_VARIABLE_PREFIX) && containsAtLeast(2)) {
            // common variable
            String token1 = peekAhead(1);
            // common variables are lowercased, not to conflict with uppercased proper variables
            name = token0LC + " " + token1.toLowerCase();
            next(2);
        }
        // "Eric" "Cooper"
        if (name == null && token0.length() > 0 && Character.isUpperCase(token0.charAt(0))) {
            // proper variable
            next(); // first part processed
            StringBuilder sb = new StringBuilder(token0.toLowerCase());
            while (!isFullyParsed()) {
                String token = peekAhead(0);
                // all parts of a Proper Name must start with capital letter
                // and must not contain keywords
                if (token.length() > 0
                        && Character.isUpperCase(token.charAt(0))
                        && !checkCurrent(Keyword._ANY_KEYWORD)) {
                    next(); // next part processed
                    // proper variables are stored lowercased
                    sb.append(" ").append(token.toLowerCase());
                } else {
                    break;
                }
            }
            name = sb.toString();
        }

        // "something" (or "it" or "self" etc)
        if (name == null) {
            // simple variables are single-word identifiers
            name = token0LC;
            next(); // first part processed
        }
        if (name != null) {
            VariableReference varRef = VariableReference.getInstance(name);
            varRef.withTokens(list, startIdx, idx);
            return varRef;
        }
        return null;
    }

    /**
     * Parses a literal or a variable name
     *
     * @return SimpleExpression on success, null otherwise
     */
    public SimpleExpression parseSimpleExpression() {
        SimpleExpression expr = parseLiteral();
        if (expr == null) {
            expr = parseVariableReference();
        }
        return expr;
    }

    Stack<CompoundExpression> operatorStack;
    Stack<Expression> valueStack;

    public Expression parse(Expression defaultExpr) {
        operatorStack = new Stack<>();
        valueStack = new Stack<>();
        if (defaultExpr != null) {
            valueStack.push(defaultExpr);
        }
        boolean isAfterOperator = true;
        while (!isFullyParsed()) {
            CompoundExpression operator = getOperator(isAfterOperator);
            if (operator == null && !isAfterOperator) {
                // two consequent values are treated as a list
                operator = (CompoundExpression) new ListExpression().withTokens(list, idx, idx);
            }
            if (operator != null) {
            	if (defaultExpr != null && valueStack.size() == 1 && operatorStack.isEmpty()) {
            		operator.setPrecedence(Precedence.COMPOUND_ASSIGNMENT);
            	}
                // operator found
                if (!pushOperator(operator)) {
                    return null;
                }
                // after operators a value is required, except FunctionCall that consumers values, too
                isAfterOperator = true;
            } else {
                SimpleExpression value = parseSimpleExpression();
                if (value != null) {
                    // value found
                    valueStack.push(value);
                } else {
                    // neither operator nor value found
                    return new ExpressionError(line, list, idx, "Operator or value required");
                }
                isAfterOperator = false;
            }
        }
        // compact operators
        if (!pushOperator(new EndOfExpression())) {
            return null;
        }
        return valueStack.isEmpty() ? null : valueStack.peek();
    }

    private boolean pushOperator(CompoundExpression operator) {

        // interpret 
        while (!operatorStack.isEmpty()) {
        	Precedence topPrec = operatorStack.peek().getPrecedence();
        	Precedence newPrec = operator.getPrecedence();
            if (operatorStack.peek() instanceof FunctionCall
                    && operator instanceof LogicalExpression
                    && ((LogicalExpression) operator).isAndExpression()) {
                break;
            }

            if ((topPrec == Precedence.NEGATION && newPrec == Precedence.NEGATION) || 
            		(topPrec == Precedence.LIST_OPERATOR && newPrec == Precedence.LIST_OPERATOR)) {
                // Logical NOT  || ListOperator (right-associative)
                break;
            }
            if (topPrec.isGreaterThan(newPrec)) {
                // other (left-associative)
                break;
            }

            // take the operator from the top of the operator stack
            CompoundExpression op = operatorStack.pop();

            // process the operator
            int paramCount = op.getParameterCount();
            if (valueStack.size() < paramCount) {
                return false;
            }
            // add param count parameters to the operator, preserving declaration order
            for (int i = 0; i < paramCount; i++) {
                op.addParameterReverse(valueStack.pop());
            }            
            // all parameters set: time to finish the setup (this may restructure the expression)
            op = op.setupFinished();
            if (op != null) {
            	setTokens(op);
                // the result of the operator is a value now
                valueStack.push(op);
            } else {
                valueStack.push(new ExpressionError(line, list, idx, "Invalid list expression"));
            }
        }

        operatorStack.push(operator);
        return true;
    }

    private void setTokens(CompoundExpression op) {
    	List<Token> tokens = new LinkedList<>();
    	tokens.addAll(op.getTokens());
    	for (Expression expr : op.getParameters()) {
			tokens.addAll(expr.getTokens());
		}
    	tokens.sort(Comparator.comparing(Token::getPos));
    	op.withTokens(tokens, 0, tokens.size());
	}

	public CompoundExpression getOperator(boolean isAfterOperator) {
		int startIdx = idx;
        CompoundExpression builtinFunction = getBuiltinFunction();
        if (builtinFunction != null) {
            return (CompoundExpression) builtinFunction.withTokens(list, startIdx, idx);
        }

        if (isAfterOperator && checkCurrent("+")) {
            // unary plus: skip
            next();
        }
        // qualifiers
        if (checkCurrent(Keyword.ON)) {
            next();
            return (CompoundExpression) new QualifierExpression(false).withTokens(list, startIdx, idx);
        }
        // array index
        if (checkCurrent(Keyword.AT)) {
            next();
            return (CompoundExpression) new QualifierExpression(true).withTokens(list, startIdx, idx);
        }
        // logical operators
        if (checkCurrent(Keyword.NOT)) {
            next();
            return (CompoundExpression) new NotExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.AND)) {
            next();
            return (CompoundExpression) new LogicalExpression(LogicalType.AND).withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.OR)) {
            next();
            return (CompoundExpression) new LogicalExpression(LogicalType.OR).withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.NOR)) {
            next();
            return (CompoundExpression) new LogicalExpression(LogicalType.NOR).withTokens(list, startIdx, idx);
        }
        boolean isIs = checkCurrent(Keyword.IS);
        boolean isIsnt = !isIs && checkCurrent(Keyword.ISNT);
        if (isIs || isIsnt) {
            next();
            boolean isNegated = isIsnt;
            while (containsAtLeast(2) && checkCurrent(Keyword.NOT)) {
                // "is not (not*) ..."
                next();
                isNegated = !isNegated;
            }
            if (containsAtLeast(3)) {
                if (checkNext(Keyword.THAN)) {
                    // "is (not*) ... than"
                    ComparisonType type = null;
                    if (checkCurrent(Keyword.HIGHER)) {
                        type = ComparisonType.GREATER_THAN;
                    } else if (checkCurrent(Keyword.LOWER)) {
                        type = ComparisonType.LESS_THAN;
                    }
                    if (type != null) {
                        next(2);
                        return (CompoundExpression) new ComparisonExpression(isNegated ? type.negated() : type)
                        		.withTokens(list, startIdx, idx);
                    }
                }
            }
            if (containsAtLeast(4)) {
                if (checkCurrent(Keyword.AS) && checkNext(2, Keyword.AS)) {
                    // "is (not*) as ... as"
                    ComparisonType type = null;
                    if (checkNext(Keyword.HIGH)) {
                        type = ComparisonType.GREATER_OR_EQUALS;
                    } else if (checkNext(Keyword.LOW)) {
                        type = ComparisonType.LESS_OR_EQUALS;
                    }
                    if (type != null) {
                        next(3);
                        return (CompoundExpression) new ComparisonExpression(isNegated ? type.negated() : type)
                        		.withTokens(list, startIdx, idx);
                    }
                }
            }
            if (containsAtLeast(2) && checkCurrent("like")) {
                // "is (not*) like"
                next();
                return (CompoundExpression) new InstanceCheckExpression(isNegated)
                		.withTokens(list, startIdx, idx);
            }
            if (containsAtLeast(4) && checkCurrent("a") && checkNext("kind") && checkNext(2, "of")) {
                // "is (not*) a kind of"
                next(3);
                return (CompoundExpression) new InstanceCheckExpression(isNegated)
                		.withTokens(list, startIdx, idx);
            }
            // simple "is" or "is not"
            return (CompoundExpression) new ComparisonExpression(isNegated ? ComparisonType.NOT_EQUALS : ComparisonType.EQUALS)
            		.withTokens(list, startIdx, idx);
        }

        // arithmetical operators
        if (isAfterOperator && checkCurrent("-")) { 
            next();
            return (CompoundExpression) new UnaryMinusExpression().withTokens(list, startIdx, idx);
        }

        if (checkCurrent(Keyword.WITH)) {
            next();
            return (CompoundExpression) new WithExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.PLUS)) {
            next();
            return (CompoundExpression) new PlusExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.INTO)) {
            next();
            return (CompoundExpression) new IntoExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.MINUS)) {
            next();
            return (CompoundExpression) new MinusExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.TIMES)) {
            next();
            return (CompoundExpression) new MultiplyExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.OVER)) {
            next();
            return (CompoundExpression) new DivideExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.ROLL)) {
            next();
            return (CompoundExpression) new RollExpression().withTokens(list, startIdx, idx);
        }
        if (checkCurrent(Keyword.FROM)) {
            next();
            return (CompoundExpression) new SliceExpression(SliceExpression.Type.SLICE_FROM).withTokens(list, startIdx, idx);
        }

        if (checkCurrent(Keyword.TILL)) {
            next();
            return (CompoundExpression) new SliceExpression(SliceExpression.Type.SLICE_TO).withTokens(list, startIdx, idx);
        }

        if (checkCurrent(",", "&")) {
            next();
            return (CompoundExpression) new ListExpression().withTokens(list, startIdx, idx);
        }

        // function call
        if (checkCurrent(Keyword.TAKING) || checkAlias(Keyword.TAKING, 0)) {
            next();
            return (CompoundExpression) new FunctionCall().withTokens(list, startIdx, idx);
        }
        return null;

    }

    private CompoundExpression getBuiltinFunction() {
        BuiltinFunction.Type type = null;
        // sorted
        if (checkCurrent(Keyword.SORTED)) {
            type = BuiltinFunction.Type.SORT;
            next();
        } // count of, length of, height of
        // last of
        else if (containsAtLeast(2) && checkNext(Keyword.OF)) {
            if (checkCurrent(Keyword.COUNT)) {
                type = BuiltinFunction.Type.SIZEOF;
                next(2);
            } else if (checkCurrent(Keyword.LAST)) {
                type = BuiltinFunction.Type.PEEK;
                next(2);
            }
        } // all keys of
        // all values of
        else if (containsAtLeast(3) && checkCurrent(Keyword.ALL) && checkNext(Keyword.OF)) {
            if (checkNext(2, Keyword.KEYS)) {
                type = BuiltinFunction.Type.KEYS;
                next(3);
            } else if (checkNext(2, Keyword.VALUES)) {
                type = BuiltinFunction.Type.VALUES;
                next(3);
            }
        }

        return type == null ? null : new BuiltinFunction(type);
    }

    private boolean checkAlias(Keyword kw, int offset) {
        if (list.size() >= idx + offset + 1) {
            List<List<String>> allAliases = block.getAliasesFor(kw);
            for (List<String> aliasParts : allAliases) {
                boolean matching = true;
                for (int i = 0; i < aliasParts.size(); i++) {
                    if (!checkNext(i, aliasParts.get(i))) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    idx += aliasParts.size() - 1;
                    return true;
                }
            }
        }
        return false;
    }

    private static class EndOfExpression extends CompoundExpression {

    	public EndOfExpression() {
			super(Precedence.END_OF_EXPRESSION);
		}

        @Override
        public String getFormat() {
            return "$";
        }

        @Override
        public int getParameterCount() {
            return 0;
        }

        @Override
        public Value evaluate(BlockContext ctx) {
            return null;
        }

        @Override
        public String format() {
            return "$";
        }
        
        @Override
        public void accept(ExpressionVisitor visitor) {
        }

    }

}

/*
  1 + 2 * 3^4           =>  1+(2*(3^4))

ValueStack                  OperatorStack
1                           
1                           +
1 2                         +
1 2                         + *
1 2 3                       + *
1 2 3                       + * ^
1 2 3 4                     + * ^
1 2 3 4                     + * ^ $ REDUCE
1 2 (3^4)                   + * $   REDUCE
1 (2*(3^4))                 + $     REDUCE
(1+(2*(3^4)))               $       FINISH


$       = 999
+ -     500
* /     400
^       300

  1*2 + 3*4             => (1*2) + (3*4)
ValueStack                  OperatorStack
1
1                           *
1 2                         *
1 2                         * +     REDUCE
(1*2)                       +
(1*2) 3                     +
(1*2) 3                     + *
(1*2) 3 4                   + *
(1*2) 3 4                   + * $   REDUCE
(1*2) (3*4)                 + $     REDUCE
((1*2)+(3*4))               $       FINISH


 not not a and b        => (!(!a)) & b

$
& | nor 
!

ValueStack                  OperatorStack
                            !
                            ! !
a                           ! !
a                           ! ! &   REDUCE
(!a)                        ! &     REDUDE
(!(!a))                     &
(!(!a)) b                   &
(!(!a)) b                   & $     REDUDE
((!(!a)) & b)               $       FINISH


  fibo taking 1 and 2 plus 3    => (fibo(1, 2) + 3)

fibo
fibo                        funccall(1,2)
fibo                        funccall +      REDUCE
fibo(1,2)                   +
fibo(1,2) 3                 +
fibo(1,2) 3                 + $             REDUCE
(fibo(1,2)+3)               $               FINISH



Midnight taking my world, Fire is nothing and Midnight taking my world, Hate is nothing


 */
