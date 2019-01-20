/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import rockstar.expression.ComparisonExpression.ComparisonType;
import rockstar.expression.LogicalExpression.LogicalType;
import rockstar.runtime.NumericValue;

/**
 *
 * @author Gabor
 */
public class ExpressionParser {

    // tokens of the whole expression
    private final List<String> list;
    // next position in the list
    private int idx;

    ExpressionParser(List<String> list) {
        this.list = list;
        idx = 0;
    }

    boolean isFullyParsed() {
        return idx == list.size();
    }

    private boolean containsAtLeast(int count) {
        return list.size() >= idx + count;
    }

    private String getCurrent() {
        return list.get(idx);
    }

    private void next() {
        idx++;
    }

    private void next(int count) {
        idx += count;
    }

    private String peekNext() {
        String next = list.get(idx + 1);
        return next;
    }

    private String peekNext(int offset) {
        String next = list.get(idx + offset);
        return next;
    }
    private static final List<String> MYSTERIOUS_KEYWORDS = Arrays.asList(new String[]{"mysterious"});
    private static final List<String> NULL_KEYWORDS = Arrays.asList(new String[]{"null", "nothing", "nowhere", "nobody", "empty", "gone"});
    private static final List<String> BOOLEAN_TRUE_KEYWORDS = Arrays.asList(new String[]{"true", "right", "yes", "ok"});
    private static final List<String> BOOLEAN_FALSE_KEYWORDS = Arrays.asList(new String[]{"false", "wrong", "no", "lies"});
    private static final List<String> RESERVED_KEYWORDS = Arrays.asList(new String[]{"definitely", "maybe"});

    ConstantValue parseLiteral() {
        if (!isFullyParsed()) {
            String token = getCurrent();
            if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                // string literal> strip quotes
                next();
                String literal = token.substring(1, token.length() - 1);
                return new ConstantValue(literal);
            }
            if (MYSTERIOUS_KEYWORDS.contains(token)) {
                next();
                return new ConstantValue(Expression.Type.MYSTERIOUS);
            }
            if (NULL_KEYWORDS.contains(token)) {
                next();
                return new ConstantValue(Expression.Type.NULL);
            }
            if (BOOLEAN_TRUE_KEYWORDS.contains(token)) {
                next();
                return new ConstantValue(true);
            }
            if (BOOLEAN_FALSE_KEYWORDS.contains(token)) {
                next();
                return new ConstantValue(false);
            }
            NumericValue nv = NumericValue.parse(token);
            if (nv != null) {
                next();
                return new ConstantValue(nv);
            }
             if (RESERVED_KEYWORDS.contains(token)) {
                 // reserved keywords are skipped
                next();
                return null;
            }
        }
        return null;
    }
    private static final List<String> COMMON_VARIABLE_KEYWORDS = Arrays.asList(new String[]{"a", "an", "the", "my", "your", "A", "An", "The", "My", "Your"});
    private static final List<String> LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS = Arrays.asList(new String[]{"it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver"});

    VariableReference parseVariableReference() {
        String name = null;
        if (isFullyParsed()) {
            return null;
        }
        String token0 = getCurrent();
        if (COMMON_VARIABLE_KEYWORDS.contains(token0) && containsAtLeast(2)) {
            // common variable
            String token1 = peekNext();
            if (token1.toLowerCase().equals(token1)) {
                name = token0.toLowerCase() + " " + token1;
                next(2);
            }
        }
        if (name == null && Character.isUpperCase(token0.charAt(0))) {
            // proper variable
            next(); // first part processed
            StringBuilder sb = new StringBuilder(token0);
            while (!isFullyParsed()) {
                String token = getCurrent();
                // all parts of a Proper Name must start with capital letter
                if (Character.isUpperCase(token.charAt(0))) {
                    next(); // next part processed
                    sb.append(" ").append(token);
                } else {
                    break;
                }
            }
            name = sb.toString();
        }
        if (name == null && containsAtLeast(1)) {
            // Variable backreference
            // TODO start of the line capitalization
            if (LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS.contains(token0)) {
                next();
                return ExpressionFactory.lastVariable;
            }
        }
        if (name != null) {
            ExpressionFactory.lastVariable = new VariableReference(name);
            return ExpressionFactory.lastVariable;
        }
        return null;
    }

    public SimpleExpression parseSimpleExpression() {
        SimpleExpression expr = parseLiteral();
        if (expr == null) {
            expr = parseVariableReference();
        }
        return expr;
    }

    Stack<CompoundExpression> operatorStack;
    Stack<Expression> valueStack;

    public Expression parse() {
        operatorStack = new Stack<>();
        valueStack = new Stack<>();
        boolean operatorRequired = false;
        while (!isFullyParsed()) {
            CompoundExpression operator = getOperator();
            if (operator != null) {
                // operator found
                pushOperator(operator);
                // after operators a value us requires, except FunctionCall that consumers values, too
                operatorRequired = (operator instanceof FunctionCall);
            } else if (operatorRequired) {
                // operator not found, but required
                return new DummyExpression(list, idx, "Operator required");
            } else {
                Expression value = parseSimpleExpression();
                if (value != null) {
                    // value found
                    valueStack.push(value);
                } else {
                    // neither operator nor value found
                    return new DummyExpression(list, idx, "Operator or value required");
                }
                operatorRequired = true;
            }
        }
        // compact operators
        pushOperator(new EndOfExpression());
        return valueStack.isEmpty() ? null : valueStack.get(0);
    }

    private void pushOperator(CompoundExpression operator) {

        // interpret 
        while (!operatorStack.isEmpty()
                && (operatorStack.peek().getPrecedence() < operator.getPrecedence())) {

            // take the operator from the top of the operator stack
            CompoundExpression op = operatorStack.pop();

            // process the operator
            int paramCount = op.getParameterCount();
            if (valueStack.size() < paramCount) {
                paramCount = valueStack.size();
            }
            // add paramcount parameters to the operator, preserving declaraton order
            for (int i = 0; i < paramCount; i++) {
                op.addParameterReverse(valueStack.pop());
            }
            // all parameters set: time to check the types
            op.setupFinished();
            // the result of the operator is a value now
            valueStack.push(op);
        }

        operatorStack.push(operator);
    }

    public CompoundExpression getOperator() {
        String operator = this.getCurrent();
        String token = this.getCurrent();
        // logical operators
        if ("not".equals(token)) {
            next();
            return new NotExpression();
        }
        if ("and".equals(token)) {
            next();
            return new LogicalExpression(LogicalType.AND);
        }
        if ("or".equals(token)) {
            next();
            return new LogicalExpression(LogicalType.OR);
        }
        if ("nor".equals(token)) {
            next();
            return new LogicalExpression(LogicalType.NOR);
        }
        if ("is".equals(token)) {
            next();
            if (containsAtLeast(3)) {
                if ("than".equals(peekNext())) {
                    String comparator = this.getCurrent();
                    ComparisonType type = null;
                    switch (comparator) {
                        case "higher":
                        case "greater":
                        case "bigger":
                        case "stronger":
                            type = ComparisonType.GREATER_THAN;
                            break;
                        case "lower":
                        case "less":
                        case "smaller":
                        case "weaker":
                            type = ComparisonType.LESS_THAN;
                            break;
                    }
                    if (type != null) {
                        next(2);
                        return new ComparisonExpression(type);
                    }
                }
            }
            if (containsAtLeast(4)) {
                if ("as".equals(getCurrent()) && "as".equals(peekNext(2))) {
                    String comparator = this.peekNext(1);
                    ComparisonType type = null;
                    switch (comparator) {
                        case "high":
                        case "great":
                        case "big":
                        case "strong":
                            type = ComparisonType.GREATER_OR_EQUALS; 
                            break;
                        case "low":
                        case "little":
                        case "small":
                        case "weak":
                            type = ComparisonType.LESS_OR_EQUALS;
                            break;
                    }
                    if (type != null) {
                        next(3);
                        return new ComparisonExpression(type);
                    }
                }
            }
            // simple "is"
            return new ComparisonExpression(ComparisonType.EQUALS);
        }
        if ("isnt".equals(token) || "aint".equals(token)) {
            next();
            return new ComparisonExpression(ComparisonType.NOT_EQUALS);
        }

        // arithmetical operators
        if ("times".equals(operator) || "of".equals(operator)) {
            next();
            return new MultiplyExpression();
        }
        if ("plus".equals(operator) || "with".equals(operator)) {
            next();
            return new PlusExpression();
        }
        if ("minus".equals(operator) || "without".equals(operator)) {
            next();
            return new MinusExpression();
        }

        // function call
        if ("taking".equals(token)) {
            next();
            FunctionCall functionCall = new FunctionCall();
            SimpleExpression funcParam;
            while (!isFullyParsed()) {
                funcParam = parseSimpleExpression();
                if (funcParam != null) {
                    functionCall.addParameter(funcParam);
                } else {
                    // ERROR: invalid parameter
                    // TODO some better method to sign expression parse error
                    valueStack.push(new DummyExpression(list, idx, "Invalid function parameter"));
                    return null;
                }
                // end of expression or no "and" found: end of parameters
                if (isFullyParsed() || !("and".equals(getCurrent()))) {
                    break;
                }
                next();
            }
            return functionCall;
        }
        return null;
    }

    private static class EndOfExpression extends CompoundExpression {

        @Override
        public int getPrecedence() {
            return 999;
        }

        @Override
        protected String getFormat() {
            return "$";
        }

        @Override
        protected int getParameterCount() {
            return 0;
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
