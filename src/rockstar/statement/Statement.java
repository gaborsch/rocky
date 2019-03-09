/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.parser.Line;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public abstract class Statement {

    private Line line;

    protected Statement() {
    }

    public final Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return explain();
    }

    public void setDebugInfo(Line line) {
        this.line = line;
    }

    /**
     * This statement is applied to the block. Statements could implement
     * specific checks if they can be applied to the block or not
     *
     * @param block
     */
    boolean applyTo(Block block) {
        return true;
    }

    /**
     * Execute the statement within the given context
     *
     * @param ctx
     */
    public abstract void execute(BlockContext ctx);

    /**
     * 
     * @param indent
     * @param explained 1: original only, 2: explained only, 3: both
     * @return 
     */
    protected final String list(int indent, int explained) {
        StringBuilder sb = new StringBuilder();
        if ((explained & 1) == 1) {
            sb.append(line == null ? "" : String.format("%3d", line.getLnum())).append(" ");
            sb.append(Utils.repeat("  ", indent));
            sb.append(line == null ? "" : line.getOrigLine().trim()).append("\n");
        }
        if ((explained & 2) == 2) {
            sb.append(line == null ? "" : String.format("%3d", line.getLnum())).append(" ");
            sb.append(Utils.repeat("  ", indent));
            sb.append(explain()).append("\n");
        }
        if (this instanceof Block) {
            List<Statement> stmts = ((Block) this).getStatements();
            ((Block) this).getStatements().forEach((stmt) -> {
                sb.append(stmt.list(indent + 1, explained));
            });
        }
        return sb.toString();
    }

    protected abstract String explain();

}
