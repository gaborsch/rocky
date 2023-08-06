package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
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
import rockstar.expression.SubtypedExpression;
import rockstar.expression.UnaryMinusExpression;
import rockstar.expression.VariableReference;
import rockstar.expression.WithExpression;
import rockstar.expression.SliceExpression.Type;
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
		List<RoleAndValue> props = new ArrayList<>();
		if (obj != null) {
			props.add(fromExpr("object", obj));
		}
		props.add(fromText("functionName",functionCall.getFunctionName()));
		props.addAll(fromExprList("param", functionCall.getParameters()));
		listValues(props);
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
		List<RoleAndValue> props = new ArrayList<>();
		if (mutationExpression.getSourceExpr() != null) {
			props.add(fromExpr("from", mutationExpression.getSourceExpr()));
		}
		if (mutationExpression.getParameterExpr() != null) {
			props.add(fromExpr("with", mutationExpression.getParameterExpr()));
		}
		if (mutationExpression.getTargetReference() != null) {
			props.add(fromExpr("into", mutationExpression.getTargetReference()));
		}
		listValues(props);
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
		listValues(fromExprList("arg", rollExpression.getParameters()));
	}

	@Override
	public void visit(SelfVariableReference selfVariableReference) {
	}

	@Override
	public void visit(SliceExpression sliceExpression) {
		SliceExpression.Type type = sliceExpression.getType();
		List<RoleAndValue> props = new ArrayList<>();
        List<Expression> parameters = sliceExpression.getParameters();
		props.add(fromExpr("base", parameters.get(0)));
        if (type != Type.SLICE_TO) {
        	props.add(fromExpr("from", parameters.get(1)));        	
        }
        if (type != Type.SLICE_FROM) {
            props.add(fromExpr("to", parameters.get(parameters.size()-1)));
        }
        list(program, props);
	}

	@Override
	public void visit(UnaryMinusExpression unaryMinusExpression) {
		listValues(fromExprList("arg", unaryMinusExpression.getParameters()));
	}

	@Override
	public void visit(VariableReference variableReference) {
		listValues(List.of(fromText("name", variableReference.getName())));
	}

	@Override
	public void visit(WithExpression withExpression) {
		listValues(fromExprList("arg", withExpression.getParameters()));
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
		List<RoleAndValue> values = new ArrayList<>(); 
		values.add(fromExpr("target", arrayAssignmentStatement.variable));
		values.addAll(fromExprList("value", arrayAssignmentStatement.expressionList));
		list(arrayAssignmentStatement, values);
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
		List<RoleAndValue> values = new ArrayList<>(); 
		values.add(fromExpr("variable", instantiationStatement.variable));
		values.add(fromExpr("className", instantiationStatement.classRef));
		values.addAll(fromExprList("parameter", instantiationStatement.ctorParameterExprs));
		list(instantiationStatement, values);
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
		List<RoleAndValue> values = new ArrayList<>(); 
		values.add(fromExpr("variable", rockStatement.variable));
		if(rockStatement.expression != null) {
			values.add(fromExpr("expression", rockStatement.expression));
		}
		list(rockStatement, values);
	}

	@Override
	public void visit(RollStatement rollStatement) {
		List<RoleAndValue> values = new ArrayList<>(); 
		values.add(fromExpr("array", rollStatement.arrayVariable));
		if(rollStatement.targetRef != null) {
			values.add(fromExpr("target", rollStatement.targetRef));
		}
		list(rollStatement, values);
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
	
	public enum ValueType {
		EXPRESSION,
		TEXT,
		LIST_OF_STRING
	}
	
	public static class RoleAndValue  {
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

	public static RoleAndValue fromExpr(String r1, Expression e1) {
		return RoleAndValue.expression(r1, e1);
	}

	public static RoleAndValue fromKeywords(String r1, List<String> e1) {
		return RoleAndValue.keywords(r1, e1);
	}
	
	public static RoleAndValue fromText(String r1, String e1) {
		return RoleAndValue.text(r1, e1);
	}
	
	public static List<RoleAndValue> fromExprList(List<? extends Expression> expressions) {
		return fromExprList("", expressions);
	}

	public static List<RoleAndValue> fromExprList(String r, List<? extends Expression> expressions) {
		AtomicInteger idx = new AtomicInteger();
		return expressions == null
				? List.of()
				: expressions.stream()
					.map(e -> RoleAndValue.expression(r + (idx.incrementAndGet()), e))
					.collect(Collectors.toList());
	}


	@Override
	public String toString() {
		return list.toString();
	}
}
