package rockstar.expression;

public interface ExpressionVisitor {

	void visit(BuiltinFunction builtinFunction);
	void visit(ComparisonExpression comparisonExpression);
	void visit(ConstantExpression constantExpression);
	void visit(DivideExpression divideExpression);
	void visit(ExpressionError expressionError);
	void visit(FunctionCall functionCall);
	void visit(InstanceCheckExpression instanceCheckExpression);
	void visit(IntoExpression intoExpression);
	void visit(LastVariableReference lastVariableReference);
	void visit(ListExpression listExpression);
	void visit(LogicalExpression logicalExpression);
	void visit(MinusExpression minusExpression);
	void visit(MultiplyExpression multiplyExpression);
	void visit(MutationExpression mutationExpression);
	void visit(NotExpression notExpression);
	void visit(PlusExpression plusExpression);
	void visit(QualifierExpression qualifierExpression);
	void visit(RollExpression rollExpression);
	void visit(SelfVariableReference selfVariableReference);
	void visit(SliceExpression sliceExpression);
	void visit(UnaryMinusExpression unaryMinusExpression);
	void visit(VariableReference variableReference);
	void visit(WithExpression withExpression);
	

}
