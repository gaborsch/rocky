/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.parser.Line;
import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public abstract class Statement {
    
    private Line line;

    protected Statement() {}

    public final Line getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (line != null) {
            line.getTokens().forEach((token) -> {
                sb.append(token).append("/");
            });
        }
        return sb.toString(); 
    }
    
    public void setDebugInfo(Line line) {
        this.line = line;      
    }

    /**
     * This statement is applied to the block. 
     * Statements could implement specific checks if they can be applied to the block or not
     * @param block 
     */
    boolean applyTo(Block block) {
        return true;
    }


    public abstract void execute(BlockContext ctx);
//    public void execute(BlockContext ctx) {
////        ctx.getOutput().println(this.getClass().getSimpleName());
////        ctx.getOutput().println(this.getClass().getSimpleName() + " " + this);
//    }
    
    public abstract String explain(BlockContext ctx);
    
    protected final String list(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line == null ? "" : String.format("%3d", line.getLnum())).append(" ").append("  ".repeat(indent)).append(list()).append("\n");
        if (this instanceof Block) {
            List<Statement> stmts = ((Block) this).getStatements();
            ((Block) this).getStatements().forEach((stmt) -> {
                sb.append(stmt.list(indent+1));
            });
        }
        return sb.toString();
    }

    protected abstract String list();
    
}
