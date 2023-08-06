package rockstar.expression;

public interface VisitableExpression {
	
	void accept(ExpressionVisitor visitor);

}
