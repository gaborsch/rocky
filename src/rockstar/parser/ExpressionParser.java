/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.util.List;
import java.util.Stack;
import rockstar.expression.BuiltinFunction;
import rockstar.expression.ComparisonExpression;
import rockstar.expression.ComparisonExpression.ComparisonType;
import rockstar.expression.CompoundExpression;
import rockstar.expression.ConstantExpression;
import rockstar.expression.DivideExpression;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionError;
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
    private final List<String> list;
    private final Line line;
    private final Block block;

    // next position in the list
    private int idx;

    ExpressionParser(List<String> list, Line line, Block block) {
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
        return kw.matches(list.get(idx));
    }

    private boolean checkCurrent(String... aliases) {
        String value = list.get(idx);
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
        return list.get(idx + 1).equalsIgnoreCase(s);
    }

    private boolean checkNext(Keyword kw) {
        return kw.matches(list.get(idx + 1));
    }

    private boolean checkNext(int offset, String s) {
        return list.get(idx + offset).equalsIgnoreCase(s);
    }

    private boolean checkNext(int offset, Keyword kw) {
        return kw.matches(list.get(idx + offset));
    }

    private String peekAhead(int offset) {
        String next = list.get(idx + offset);
        return next;
    }

    /**
     * Parses a String, numeric, bool, null or mysterious literal
     *
     * @return ConstantExpression on success, null otherwise
     */
    ConstantExpression parseLiteral() {
        if (!isFullyParsed()) {
            String token = peekAhead(0);
            if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                // string literal> strip quotes
                next();
                String literal = token.substring(1, token.length() - 1);
                // replace escaped backslash sequences with characters
                // negative lookbehind (?<!): should not match if preceded with \\
                literal = literal.replace("(?<!\\)\\t", "\t").replace("(?<!\\)\\r", "\r").replace("(?<!\\)\\n", "\n").replace("\\\\", "\\");
                return new ConstantExpression(literal);
            }
            if (checkCurrent(Keyword.MYSTERIOUS)) {
                next();
                return ConstantExpression.CONST_MYSTERIOUS;
            }
            if (checkCurrent(Keyword.EMPTY_STRING)) {
                next();
                return ConstantExpression.CONST_EMPTY_STRING;
            }
            if (checkCurrent(Keyword.NULL)) {
                next();
                return ConstantExpression.CONST_NULL;
            }
            if (checkCurrent(Keyword.EMPTY_ARRAY)) {
                next();
                return ConstantExpression.CONST_EMPTY_ARRAY;
            }
            if (checkCurrent(Keyword.BOOLEAN_TRUE)) {
                next();
                return ConstantExpression.CONST_TRUE;
            }
            if (checkCurrent(Keyword.BOOLEAN_FALSE)) {
                next();
                return ConstantExpression.CONST_FALSE;
            }
            RockNumber nv = RockNumber.parse(peekAhead(0).toLowerCase());
            if (nv != null) {
                next();
                return new ConstantExpression(nv);
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
                        && !checkCurrent(Keyword._STARTER_KEYWORD)) {
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
                operator = new ListExpression();
            }
            if (operator != null) {
                // operator found
                if (!pushOperator(operator)) {
                    return null;
                }
                // after operators a value is required, except FunctionCall that consumers values, too
                isAfterOperator = true;
            } else {
                Expression value = parseSimpleExpression();
                if (value != null) {
                    // value found
                    valueStack.push(value);
                } else {
                    // neither operator nor value found
                    return new ExpressionError(list, idx, "Operator or value required");
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
            int topPrec = operatorStack.peek().getPrecedence();
            int newPrec = operator.getPrecedence();
            if (operatorStack.peek() instanceof FunctionCall
                    && operator instanceof LogicalExpression
                    && ((LogicalExpression) operator).getType() == LogicalType.AND) {
                break;
            }

            if ((topPrec == 600 && newPrec == 600) || (topPrec == 80 && newPrec == 80)) {
                // Logical NOT  || ListOperator (right-associative)
                break;
            }
            if (topPrec > newPrec) {
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
            // add paramcount parameters to the operator, preserving declaraton order
            for (int i = 0; i < paramCount; i++) {
                op.addParameterReverse(valueStack.pop());
            }
            // all parameters set: time to finish the setup (this may restructure the expression)
            op = op.setupFinished();
            if (op != null) {
                // the result of the operator is a value now
                valueStack.push(op);
            } else {
                valueStack.push(new ExpressionError(list, idx, "Invalid list expression"));
            }
        }

        operatorStack.push(operator);
        return true;
    }

    public CompoundExpression getOperator(boolean isAfterOperator) {

        CompoundExpression builtinFunction = getBuiltinFunction();
        if (builtinFunction != null) {
            return builtinFunction;
        }

        if (isAfterOperator && checkCurrent("+")) {
            // unary plus: skip
            next();
        }
        // qualifiers
        if (checkCurrent(Keyword.ON)) {
            next();
            return new QualifierExpression(false);
        }
        // array index
        if (checkCurrent(Keyword.AT)) {
            next();
            return new QualifierExpression(true);
        }
        // logical operators
        if (checkCurrent(Keyword.NOT)) {
            next();
            return new NotExpression();
        }
        if (checkCurrent(Keyword.AND)) {
            next();
            return new LogicalExpression(LogicalType.AND);
        }
        if (checkCurrent(Keyword.OR)) {
            next();
            return new LogicalExpression(LogicalType.OR);
        }
        if (checkCurrent(Keyword.NOR)) {
            next();
            return new LogicalExpression(LogicalType.NOR);
        }
        boolean isIs = checkCurrent(Keyword.IS);
        boolean isIsnt = checkCurrent(Keyword.ISNT);
        if (isIs || isIsnt) {
            next();
            boolean isNegated = isIsnt;
            while (containsAtLeast(2) && checkCurrent(Keyword.NOT)) {
                // "is not ..."
                next();
                isNegated = !isNegated;
            }
            if (containsAtLeast(3)) {
                if (checkNext(Keyword.THAN)) {
                    // "is ... than"
                    ComparisonType type = null;
                    if (checkCurrent(Keyword.HIGHER)) {
                        type = ComparisonType.GREATER_THAN;
                    } else if (checkCurrent(Keyword.LOWER)) {
                        type = ComparisonType.LESS_THAN;
                    }
                    if (type != null) {
                        next(2);
                        return new ComparisonExpression(isNegated ? type.negated() : type);
                    }
                }
            }
            if (containsAtLeast(4)) {
                if (checkCurrent(Keyword.AS) && checkNext(2, Keyword.AS)) {
                    // "is as ... as"
                    ComparisonType type = null;
                    if (checkNext(Keyword.HIGH)) {
                        type = ComparisonType.GREATER_OR_EQUALS;
                    } else if (checkNext(Keyword.LOW)) {
                        type = ComparisonType.LESS_OR_EQUALS;
                    }
                    if (type != null) {
                        next(3);
                        return new ComparisonExpression(isNegated ? type.negated() : type);
                    }
                }
            }
            if (containsAtLeast(2) && checkCurrent("like")) {
                // "is [not] like"
                next();
                return isNegated
                        ? new NotExpression(new InstanceCheckExpression())
                        : new InstanceCheckExpression();
            }
            if (containsAtLeast(4) && checkCurrent("a") && checkNext("kind") && checkNext(2, "of")) {
                // "is [not] a kind of"
                next(3);
                return isNegated
                        ? new NotExpression(new InstanceCheckExpression())
                        : new InstanceCheckExpression();
            }
            // simple "is" or "is not"
            return new ComparisonExpression(isNegated ? ComparisonType.NOT_EQUALS : ComparisonType.EQUALS);
        }

        // arithmetical operators
        if (isAfterOperator && checkCurrent("-")) {
            // unary minus
            next();
            return new UnaryMinusExpression();
        }

        if (checkCurrent(Keyword.WITH)) {
            next();
            return new WithExpression();
        }
        if (checkCurrent(Keyword.PLUS)) {
            next();
            return new PlusExpression();
        }
        if (checkCurrent(Keyword.INTO)) {
            next();
            return new IntoExpression();
        }
        if (checkCurrent(Keyword.MINUS)) {
            next();
            return new MinusExpression();
        }
        if (checkCurrent(Keyword.TIMES)) {
            next();
            return new MultiplyExpression();
        }
        if (checkCurrent(Keyword.OVER)) {
            next();
            return new DivideExpression();
        }
        if (checkCurrent(Keyword.ROLL)) {
            next();
            return new RollExpression();
        }
        if (checkCurrent(Keyword.FROM)) {
            next();
            return new SliceExpression(SliceExpression.Type.SLICE_FROM);
        }

        if (checkCurrent(Keyword.TILL)) {
            next();
            return new SliceExpression(SliceExpression.Type.SLICE_TO);
        }

        if (checkCurrent(",")) {
            next();
            return new ListExpression();
        }

        // function call
        if (checkCurrent(Keyword.TAKING) || checkAlias(Keyword.TAKING, 0)) {
            next();
            return new FunctionCall();
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

        @Override
        public int getPrecedence() {
            return 999;
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
