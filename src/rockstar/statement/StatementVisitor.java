package rockstar.statement;

import rockstar.expression.Expression;

public interface StatementVisitor {

	void visit(AliasStatement aliasStatement);
	void visit(ArrayAssignmentStatement arrayAssignmentStatement);
	void visit(AssignmentStatement assignmentStatement);
	void visit(BlockEnd blockEnd);
	void visit(BreakStatement breakStatement);
	void visit(CastStatement castStatement);
	void visit(ClassBlock classBlock);
	void visit(ContinueStatement continueStatement);
	void visit(DecrementStatement decrementStatement);
	void visit(ElseStatement elseStatement);
	void visit(ExpressionStatement expressionStatement);
	void visit(FunctionBlock functionBlock);
	void visit(IfStatement ifStatement);
	void visit(ImportStatement importStatement);
	void visit(IncrementStatement incrementStatement);
	void visit(InstantiationStatement instantiationStatement);
	void visit(InvalidStatement invalidStatement);
	void visit(IterateStatement iterateStatement);
	void visit(JoinStatement joinStatement);
	void visit(ListenStatement listenStatement);
	void visit(PkgDefStatement pkgDefStatement);
	void visit(Program program);
	void visit(ReturnStatement returnStatement);
	void visit(RockStatement rockStatement);
	void visit(RollStatement rollStatement);
	void visit(SayStatement sayStatement);
	void visit(SplitStatement splitStatement);
	void visit(StatementError statementError);
	void visit(TurnStatement turnStatement);
	void visit(WhileStatement whileStatement);


}
