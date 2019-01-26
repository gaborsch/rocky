/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

/**
 *
 * @author Gabor
 */
public class BreakStatement extends Statement {

    @Override
    boolean applyTo(Block block) {
        Block b = block;
        while (b != null) {
            if (b instanceof WhileStatement) {
                return true;
            }
            b = b.getParent();
        }
        return false;
    }

}
