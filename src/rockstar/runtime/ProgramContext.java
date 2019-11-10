/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.statement.ClassBlock;
import rockstar.statement.Program;

/**
 *
 * @author Gabor
 */
public class ProgramContext extends BlockContext {

    protected final Map<QualifiedClassName, ClassBlock> classes = new HashMap<>();
    private final Set<QualifiedClassName> beingLoaded = new HashSet<>();

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
        return classes.get(qcn);
    }

    /**
     * Load a class, if necessary
     *
     * @param qcn
     */
    public void importClass(QualifiedClassName qcn) {
        if (!classes.containsKey(qcn)) {
            // to avoid circular references, already referred classes are not loaded again
            if (!beingLoaded.contains(qcn)) {
                beingLoaded.add(qcn);
                loadClass(qcn);
                beingLoaded.remove(qcn);
            }
        }
    }

    private void loadClass(QualifiedClassName qcn) {
        String envPath = getEnv().getParameter("-libpath");
        String libRoot = envPath == null ? "rockstar-lib" : envPath;
        loadClass(libRoot, qcn);
    }

    private void loadClass(String libRoot, QualifiedClassName qcn) {
        final String filePath = libRoot + "/" + qcn.getFormattedFilename() + ".rock";
        try {
            Program prg = new Parser(filePath).parse();
            FileContext fileCtx = new FileContext(this, qcn.getName());
            prg.execute(fileCtx);
        } catch (FileNotFoundException ex) {
            throw new RockstarRuntimeException("Error loading file " + filePath+", File not found");
        } catch (ParseException pex) {
            throw new RockstarRuntimeException("Error loading file " + filePath+", Parse error: "+pex.getMessage());
        }
    }
}
