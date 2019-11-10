/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Map;
import rockstar.statement.ClassBlock;

/**
 *
 * @author Gabor
 */
public class ProgramContext extends BlockContext {

    protected final Map<QualifiedClassName, ClassBlock> classes = new HashMap<>();

    protected ProgramContext(Environment env) {
        super(env);
    }

    public void defineClass(QualifiedClassName qcn, ClassBlock classBlock) {
        this.classes.put(qcn, classBlock);
    }

    public Map<QualifiedClassName, ClassBlock> getClasses() {
        return classes;
    }

    public ClassBlock retrieveClass(QualifiedClassName qcn) {
        if (qcn == null) {
            return null;
        }
        // try to find if it has been loaded already
        ClassBlock c = classes.get(qcn);
        return c;
    }

    
}
