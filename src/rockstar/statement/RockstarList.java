package rockstar.statement;

import java.util.List;

import rockstar.parser.Line;
import rockstar.parser.ParserError;
import rockstar.runtime.Utils;

public class RockstarList implements StatementVisitor {
	
	private final boolean lineNums;
	
	private Program program;
	private String lineNumberFormat = "(%d)";
	private int lnum = 1;
	private int indent = -1;
	
	private StringBuilder list = new StringBuilder();
	
	public RockstarList(Program program, boolean lineNums) {
		super();
		this.program = program;
		this.lineNums = lineNums;
		setLineNumberFormat();
	}
	
	private StringBuilder nl() {
		return list.append(System.lineSeparator());
	}

	private StringBuilder indent() {
		return list.append(Utils.repeat("  ", indent));
	}
	
	private int lineNumber(Line line) {
		int current = line.getLnum();
		while(lnum < current) {
			list.append(String.format(lineNumberFormat, lnum));
			nl();
			lnum++;
		}
		if (lineNums) {
			list.append(String.format(lineNumberFormat, lnum));
			return lineNumberFormat.length();
		}
		return 0;
	}
	
	private void list(Statement stmt) {
		Line line = stmt.getLine();
		if (line != null) {
			int lineNumIndent = lineNumber(line);
			indent().append(line.getOrigLine());
			ParserError error = program.getErrorOnLine(lnum);
            if (error != null) {
            	nl()
            		.append(Utils.repeat(" ", lineNumIndent + error.getPos()))
            		.append("^--- Error: ")
            		.append(error.getMsg());
            }
		}
	}

	private void list(Block block) {
		list((Statement)block);
		indent++;
		block.getStatements().forEach(s -> s.accept(this));
		indent--;
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
		int maxLineNum = s.getLine() == null ? 1 : s.getLine().getLnum();
		int width = (maxLineNum < 10 ? 2 : (maxLineNum < 100 ? 2 : (maxLineNum < 1000 ? 3 : 4)));
		this.lineNumberFormat = "(%" + width + "d) ";
	}
	
	@Override
	public void visit(Program program) {
		list(program);
	}

	@Override
	public void visit(AliasStatement aliasStatement) {
		list(aliasStatement);
	}

	@Override
	public void visit(ArrayAssignmentStatement arrayAssignmentStatement) {
		list(arrayAssignmentStatement);
	}

	@Override
	public void visit(AssignmentStatement assignmentStatement) {
		list(assignmentStatement);
	}

	@Override
	public void visit(BlockEnd blockEnd) {
		list(blockEnd);
	}

	@Override
	public void visit(BreakStatement breakStatement) {
		list(breakStatement);
	}

	@Override
	public void visit(CastStatement castStatement) {
		list(castStatement);
	}

	@Override
	public void visit(ClassBlock classBlock) {
		list(classBlock);
	}

	@Override
	public void visit(ContinueStatement continueStatement) {
		list(continueStatement);
	}

	@Override
	public void visit(DecrementStatement decrementStatement) {
		list(decrementStatement);
	}

	@Override
	public void visit(ElseStatement elseStatement) {
		list(elseStatement);
	}

	@Override
	public void visit(ExpressionStatement expressionStatement) {
		list(expressionStatement);
	}

	@Override
	public void visit(FunctionBlock functionBlock) {
		list(functionBlock);
	}

	@Override
	public void visit(IfStatement ifStatement) {
		list(ifStatement);
	}

	@Override
	public void visit(ImportStatement importStatement) {
		list(importStatement);
	}

	@Override
	public void visit(IncrementStatement incrementStatement) {
		list(incrementStatement);
	}

	@Override
	public void visit(InstantiationStatement instantiationStatement) {
		list(instantiationStatement);
	}

	@Override
	public void visit(InvalidStatement invalidStatement) {
		list(invalidStatement);
	}

	@Override
	public void visit(IterateStatement iterateStatement) {
		list(iterateStatement);
	}

	@Override
	public void visit(JoinStatement joinStatement) {
		list(joinStatement);
	}

	@Override
	public void visit(ListenStatement listenStatement) {
		list(listenStatement);
	}

	@Override
	public void visit(PkgDefStatement pkgDefStatement) {
		list(pkgDefStatement);
	}

	@Override
	public void visit(ReturnStatement returnStatement) {
		list(returnStatement);
	}

	@Override
	public void visit(RockStatement rockStatement) {
		list(rockStatement);
	}

	@Override
	public void visit(RollStatement rollStatement) {
		list(rollStatement);
	}

	@Override
	public void visit(SayStatement sayStatement) {
		list(sayStatement);
	}

	@Override
	public void visit(SplitStatement splitStatement) {
		list(splitStatement);
	}

	@Override
	public void visit(StatementError statementError) {
		list(statementError);
	}

	@Override
	public void visit(TurnStatement turnStatement) {
		list(turnStatement);
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		list(whileStatement);
	}

	public StringBuilder list() {
		visit(program);
		return list;
	}

}
