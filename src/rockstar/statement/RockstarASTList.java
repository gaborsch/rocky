package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import rockstar.expression.BuiltinFunction;
import rockstar.expression.ComparisonExpression;
import rockstar.expression.ConstantExpression;
import rockstar.expression.DivideExpression;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionError;
import rockstar.expression.ExpressionVisitor;
import rockstar.expression.FunctionCall;
import rockstar.expression.InstanceCheckExpression;
import rockstar.expression.IntoExpression;
import rockstar.expression.LastVariableReference;
import rockstar.expression.ListExpression;
import rockstar.expression.LogicalExpression;
import rockstar.expression.MinusExpression;
import rockstar.expression.MultiplyExpression;
import rockstar.expression.MutationExpression;
import rockstar.expression.NotExpression;
import rockstar.expression.PlusExpression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.RollExpression;
import rockstar.expression.SelfVariableReference;
import rockstar.expression.SliceExpression;
import rockstar.expression.SliceExpression.Type;
import rockstar.expression.SubtypedExpression;
import rockstar.expression.UnaryMinusExpression;
import rockstar.expression.VariableReference;
import rockstar.expression.WithExpression;
import rockstar.parser.Line;
import rockstar.parser.ParserError;
import rockstar.runtime.Utils;

public class RockstarASTList implements StatementVisitor, ExpressionVisitor {
	
	private Program program;
	private String lineNumberFormat = "(%d)";
	private int lineNumberFormatLength = 0;
	private int lnum = 0;
	private int lineIndent = -1;

	private String expressionIndent = ""; 
	
	private StringBuilder list = new StringBuilder();
	
	public RockstarASTList(Program program) {
		super();
		this.program = program;
		setLineNumberFormat();
	}
	
	private StringBuilder nl() {
		if (!list.isEmpty()) {
			return list.append(System.lineSeparator());
		}
		return list;
	}

	private StringBuilder lineIndent() {
		return list.append(Utils.repeat("  ", lineIndent));
	}
	
	private void appendLineNumber(Line line) {
		int current = line.getLnum();
		while(lnum < current) {
			nl().append(String.format(lineNumberFormat, ++lnum));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<RoleAndValue> toValues(Object... values) {
		List<RoleAndValue> valueList = new ArrayList<>();
		Boolean nextAccepted = true;
		for (Object o : values) {
			if (nextAccepted && o != null) {
				if (o instanceof List) {
					valueList.addAll((List<RoleAndValue>) o);
				} else if (o instanceof RoleAndValue) {
					valueList.add((RoleAndValue) o);
				} else if (o instanceof Boolean) {
					nextAccepted = (Boolean) o;
				}
			}
			nextAccepted = true;
		}
		return valueList;
	}
	
	private void list(Statement stmt, List<RoleAndValue> values) {
		Line line = stmt.getLine();
		if (line != null) {
			appendLineNumber(line);
			lineIndent().append(line.getOrigLine());
			ParserError error = program.getErrorOnLine(lnum);
            if (error != null) {
            	nl()
            		.append(Utils.repeat(" ", lineNumberFormatLength + error.getPos()))
            		.append("^--- Error: ")
            		.append(error.getMsg());
            }
            nl();
            lineIndent().append(Utils.repeat(" ", lineNumberFormatLength))
            	.append(getStatementDisplayName(stmt));
            listValues(Utils.repeat(" ", lineNumberFormatLength), values);
		}
	}

	private String getStatementDisplayName(Statement stmt) {
		return stmt.getStatementDisplayText();
	}

	private void list(Block block, List<RoleAndValue> values) {
		list((Statement) block, values);
		lineIndent++;
		block.getStatements().forEach(s -> s.accept(this));
		lineIndent--;
	}
	
	private void listValue(String nodeIndent, RoleAndValue rav) {
    	nl();
    	lineIndent().append(expressionIndent).append(nodeIndent).append(rav.role).append(": ");
    	switch (rav.type) {    	
		case EXPRESSION:
			Expression e = (Expression) rav.value;
			list.append(getExpressionDisplayName(e));
			break;
		case LIST_OF_STRING:
			@SuppressWarnings("unchecked") 
			List<String> l = (List<String>) rav.value;
			StringJoiner j = new StringJoiner(" ");
			l.forEach(j::add);
			list.append(j.toString());
			break;
		case TEXT:
			list.append((String)rav.value);
			break;
		default:
			break;
		}
	}
	
	private String getExpressionDisplayName(Expression e) {
		if (e instanceof SubtypedExpression) {
			return ((SubtypedExpression) e).getType();
		}
		return e.getClass().getSimpleName()
				.replaceFirst("Expression", "")
				.replaceFirst("Reference", "")
				.toUpperCase();
	}

	private void listValues(String indent, List<RoleAndValue> values) {
		this.expressionIndent = indent;
		listValues(values);
	}
	
	private void listValues(List<RoleAndValue> values) {
		final String oldExpressionIndent = this.expressionIndent; 
        int lastIdx = values.size() - 1;
        for (int i = 0; i < values.size(); i++) {
        	RoleAndValue rav = values.get(i);
        	String nodeIndent = i == lastIdx ? "\\---" : "+---";
        	String subNodeIndent = i == lastIdx ? "    " : "|   ";
            this.expressionIndent = oldExpressionIndent;
        	listValue(nodeIndent, rav);
        	this.expressionIndent = oldExpressionIndent + subNodeIndent;
        	if (rav.type == ValueType.EXPRESSION) {
        		((Expression)rav.value).accept(this);
        	}
		}
        this.expressionIndent = oldExpressionIndent;
	}
	
	private void setLineNumberFormat() {
		Statement s = program;
		while (s instanceof Block) {
			List<Statement> l = ((Block) s).getStatements();
			if (l.isEmpty()) {
				break;
			}
			s = l.get(l.size() - 1);
		}
		int maxLineNum = s.getLine().getLnum();
		int width = (maxLineNum < 10 ? 2 : (maxLineNum < 100 ? 2 : (maxLineNum < 1000 ? 3 : 4)));
		this.lineNumberFormat = "(%0" + width + "d) ";
		this.lineNumberFormatLength = width + 3;
	}
	
	

	@Override
	public void visit(BuiltinFunction builtinFunction) {
		listValues(fromExprList("param", builtinFunction.getParameters()));
	}

	@Override
	public void visit(ComparisonExpression comparisonExpression) {
		listValues(fromExprList("arg", comparisonExpression.getParameters()));
	}

	@Override
	public void visit(ConstantExpression constantExpression) {
		listValues(List.of(
				fromText("type", constantExpression.getValue().getType().name()),
				fromText("value", constantExpression.getValue().toString())));
	}

	@Override
	public void visit(DivideExpression divideExpression) {
		listValues(fromExprList("arg", divideExpression.getParameters()));
	}

	@Override
	public void visit(ExpressionError expressionError) {
	}

	@Override
	public void visit(FunctionCall functionCall) {
		VariableReference obj = functionCall.getObject();
		listValues(toValues(
				fromExpr("object", obj),
				fromText("functionName",functionCall.getFunctionName()),
				fromExprList("param", functionCall.getParameters())));
	}

	@Override
	public void visit(InstanceCheckExpression instanceCheckExpression) {
		listValues(List.of(
				fromExpr("object", instanceCheckExpression.getObjectRef()),
				fromExpr("class", instanceCheckExpression.getClassRef()),
				fromText("negated", Boolean.toString(instanceCheckExpression.isNegated()))));
	}

	@Override
	public void visit(IntoExpression intoExpression) {
		// IntoExpression does not occur in the final expression
	}

	@Override
	public void visit(LastVariableReference lastVariableReference) {
	}

	@Override
	public void visit(ListExpression listExpression) {
		listValues(fromExprList("item", listExpression.getParameters()));
	}

	@Override
	public void visit(LogicalExpression logicalExpression) {
		listValues(fromExprList("arg", logicalExpression.getParameters()));
	}

	@Override
	public void visit(MinusExpression minusExpression) {
		listValues(fromExprList("arg", minusExpression.getParameters()));
	}

	@Override
	public void visit(MultiplyExpression multiplyExpression) {
		listValues(fromExprList("arg", multiplyExpression.getParameters()));
	}

	@Override
	public void visit(MutationExpression mutationExpression) {
		listValues(toValues(
				fromExpr("from", mutationExpression.getSourceExpr()),
				fromExpr("with", mutationExpression.getParameterExpr()),
				fromExpr("into", mutationExpression.getTargetReference())));
	}

	@Override
	public void visit(NotExpression notExpression) {
		listValues(fromExprList("condition", notExpression.getParameters()));
	}

	@Override
	public void visit(PlusExpression plusExpression) {
		listValues(fromExprList("arg", plusExpression.getParameters()));
	}

	@Override
	public void visit(QualifierExpression qualifierExpression) {
		if (qualifierExpression.isArrayIndexing()) {
			listValues(List.of(
					fromExpr("array", qualifierExpression.getArrayBaseRef()),
					fromExpr("index", qualifierExpression.getArrayIndexRef())));
		} else {
			listValues(List.of(
					fromExpr("object", qualifierExpression.getObjectRef()),
					fromExpr("method", qualifierExpression.getMethodRef())));
		}
	}

	@Override
	public void visit(RollExpression rollExpression) {
		listValues(fromExprList("expr", rollExpression.getParameters()));
	}

	@Override
	public void visit(SelfVariableReference selfVariableReference) {
	}

	@Override
	public void visit(SliceExpression sliceExpression) {
		SliceExpression.Type type = sliceExpression.getType();
		List<Expression> parameters = sliceExpression.getParameters();
        list(program, toValues(
        		fromExpr("base", parameters.get(0)),
        		type != Type.SLICE_TO,
				fromExpr("from", parameters.get(1)),
				type != Type.SLICE_FROM,
				fromExpr("to", parameters.get(parameters.size()-1))));
	}

	@Override
	public void visit(UnaryMinusExpression unaryMinusExpression) {
		listValues(fromExprList("expr", unaryMinusExpression.getParameters()));
	}

	@Override
	public void visit(VariableReference variableReference) {
		listValues(List.of(fromText("name", variableReference.getName())));
	}

	@Override
	public void visit(WithExpression withExpression) {
		listValues(fromExprList("expr", withExpression.getParameters()));
	}

	@Override
	public void visit(Program program) {
		list(program, List.of());
	}

	@Override
	public void visit(AliasStatement aliasStatement) {
		list(aliasStatement,
				List.of(fromKeywords("old", aliasStatement.keyword), 
						fromKeywords("new", aliasStatement.alias)));
	}

	@Override
	public void visit(ArrayAssignmentStatement arrayAssignmentStatement) {
		list(arrayAssignmentStatement, toValues(
				fromExpr("target", arrayAssignmentStatement.variable),
				fromExprList("element", arrayAssignmentStatement.expressionList)));
	}

	@Override
	public void visit(AssignmentStatement assignmentStatement) {
		list(assignmentStatement, 
				List.of(fromExpr("target", assignmentStatement.variableExpression),
						fromExpr("value", assignmentStatement.valueExpression)));
	}

	@Override
	public void visit(BlockEnd blockEnd) {
		list(blockEnd, List.of());
	}

	@Override
	public void visit(BreakStatement breakStatement) {
		list(breakStatement, List.of());
	}

	@Override
	public void visit(CastStatement castStatement) {
		list(castStatement, List.of(fromExpr("value", castStatement.expr)));
	}

	@Override
	public void visit(ClassBlock classBlock) {
		list(classBlock, 
				List.of(fromText("class", classBlock.name),
						fromText("parentClass", classBlock.parentName)));
	}

	@Override
	public void visit(ContinueStatement continueStatement) {
		list(continueStatement,List.of());
	}

	@Override
	public void visit(DecrementStatement decrementStatement) {
		list(decrementStatement, 
				List.of(fromExpr("variable", decrementStatement.variable),
						fromText("decrementBy", Integer.toString(decrementStatement.count))));
	}

	@Override
	public void visit(ElseStatement elseStatement) {
		list(elseStatement, List.of());
	}

	@Override
	public void visit(ExpressionStatement expressionStatement) {
		list(expressionStatement, 
				List.of(fromExpr("value", expressionStatement.expression)));
	}

	@Override
	public void visit(FunctionBlock functionBlock) {
		List<RoleAndValue> values = new ArrayList<>(); 
		values.add(fromText("functionName", functionBlock.getName()));
		values.addAll(fromExprList("parameter", functionBlock.getParameterRefs()));
		list(functionBlock, values);
	}

	@Override
	public void visit(IfStatement ifStatement) {
		list(ifStatement, 
				List.of(fromExpr("condition", ifStatement.getCondition())));
	}

	@Override
	public void visit(ImportStatement importStatement) {		
		list(importStatement, 
				List.of(
					fromKeywords("packagePath", importStatement.path.getPath()),
					fromKeywords("classNames", importStatement.names)));
	}

	@Override
	public void visit(IncrementStatement incrementStatement) {
		list(incrementStatement, 
				List.of(fromExpr("variable", incrementStatement.variable),
						fromText("incrementBy", Integer.toString(incrementStatement.count))));
	}

	@Override
	public void visit(InstantiationStatement instantiationStatement) {
		list(instantiationStatement, toValues(
				fromExpr("variable", instantiationStatement.variable),
				fromExpr("className", instantiationStatement.classRef),
				fromExprList("parameter", instantiationStatement.ctorParameterExprs)));
	}

	@Override
	public void visit(InvalidStatement invalidStatement) {
		list(invalidStatement, List.of());
	}

	@Override
	public void visit(IterateStatement iterateStatement) {
		list(iterateStatement, 
				List.of(
					fromExpr("variable", iterateStatement.asExpr),
					fromExpr("array", iterateStatement.arrayExpr)));
	}

	@Override
	public void visit(JoinStatement joinStatement) {
		list(joinStatement, List.of(fromExpr("value", joinStatement.expr)));
	}

	@Override
	public void visit(ListenStatement listenStatement) {
		list(listenStatement, 
				List.of(fromExpr("variable", listenStatement.variable)));
	}

	@Override
	public void visit(PkgDefStatement pkgDefStatement) {
		list(pkgDefStatement, 
				List.of(fromKeywords("packagePath", pkgDefStatement.path.getPath())));
	}

	@Override
	public void visit(ReturnStatement returnStatement) {
		list(returnStatement, 
				List.of(fromExpr("value", returnStatement.expression)));
	}

	@Override
	public void visit(RockStatement rockStatement) {
		list(rockStatement, toValues(
				fromExpr("variable", rockStatement.variable),
				fromExpr("expression", rockStatement.expression)));
	}

	@Override
	public void visit(RollStatement rollStatement) {
		list(rollStatement, toValues(
				fromExpr("array", rollStatement.arrayVariable),
				fromExpr("target", rollStatement.targetRef)));
	}

	@Override
	public void visit(SayStatement sayStatement) {
		list(sayStatement, 
				List.of(fromExpr("value", sayStatement.expression)));
	}

	@Override
	public void visit(SplitStatement splitStatement) {
		list(splitStatement, List.of(fromExpr("value", splitStatement.expr)));
	}

	@Override
	public void visit(StatementError statementError) {
		list(statementError, List.of());
	}

	@Override
	public void visit(TurnStatement turnStatement) {
		list(turnStatement, 
				List.of(
					fromExpr("variable", turnStatement.variable),
					fromText("direction", turnStatement.direction.name())));
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		list(whileStatement, List.of(fromExpr("condition", whileStatement.condition)));
	}

	public StringBuilder list() {
		visit(program);
		return list;
	}
	
	private enum ValueType {
		EXPRESSION,
		TEXT,
		LIST_OF_STRING
	}
	
	private static class RoleAndValue  {
		public ValueType type;
		public String role;
		public Object value;
		
		private RoleAndValue(ValueType type, String role, Object value) {
			this.type = type;
			this.role = role;
			this.value = value;
		}
		
		public static RoleAndValue expression(String role, Expression expr) {
			return new RoleAndValue(ValueType.EXPRESSION, role, expr);
		}

		public static RoleAndValue text(String role, String text) {
			return new RoleAndValue(ValueType.TEXT, role, text);
		}

		public static RoleAndValue keywords(String role, List<String> kws) {
			return new RoleAndValue(ValueType.LIST_OF_STRING, role, kws);
		}
	}

	private static RoleAndValue fromExpr(String r, Expression e) {
		return e == null ? null : RoleAndValue.expression(r, e);
	}

	private static RoleAndValue fromKeywords(String r, List<String> kl) {
		return kl == null || kl.isEmpty() ? null : RoleAndValue.keywords(r, kl);
	}
	
	private static RoleAndValue fromText(String r, String t) {
		return t == null ? null : RoleAndValue.text(r, t);
	}
	
	private static List<RoleAndValue> fromExprList(String r, List<? extends Expression> expressions) {
		AtomicInteger idx = new AtomicInteger();
		return expressions == null
				? List.of()
				: expressions.stream()
					.filter(Objects::nonNull)
					.map(e -> RoleAndValue.expression(r + (idx.incrementAndGet()), e))
					.collect(Collectors.toList());
	}


	@Override
	public String toString() {
		return list.toString();
	}
}
