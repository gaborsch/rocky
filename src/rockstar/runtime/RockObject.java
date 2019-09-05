/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.statement.ClassBlock;

/**
 *
 * @author Gabor
 */
public class RockObject extends BlockContext {

    public RockObject(ClassBlock block) {
        super(null, block.getName());
        block.getMethods().forEach((method) -> {
            this.defineFunction(method.getName(), method);
        });
    }
}
