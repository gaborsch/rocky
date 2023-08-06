package rockstar.statement;

public interface VisitableStatement {
	
	void accept(StatementVisitor visitor);

}
