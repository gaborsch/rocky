/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        // first load from system lib
        try {
            // the system lib can be overriden
            String libPath = getEnv().getParameter("-libpath");
            if (libPath == null || libPath.isEmpty()) {
                // if the libpath is not present, use system classes
                loadClass(this.getClass().getClassLoader(), qcn);
            } else {
                // otherwise use the given path
                loadClass(libPath, qcn);
            }
        } catch (FileNotFoundException e1) {
            // if not found, try to load from current folder
            try {
                loadClass(".", qcn);
            } catch (FileNotFoundException e2) {
                throw new RockstarRuntimeException("File " + qcn.getFormattedFilename() + " not found");
            }
        }

    }

    private void loadClass(String libRoot, QualifiedClassName qcn) throws FileNotFoundException {
        final String filePath = libRoot + "/" + qcn.getFormattedFilename();
        loadClass(new FileInputStream(new File(filePath)), qcn.getName());
    }

    private void loadClass(ClassLoader cl, QualifiedClassName qcn) throws FileNotFoundException {
        final String filePath = "rockstar-lib/" + qcn.getFormattedFilename();
        InputStream is = cl.getResourceAsStream(filePath);
        loadClass(is, qcn.getName());
    }

    private void loadClass(InputStream is, String filename) throws FileNotFoundException {
        try {
            Program prg = new Parser(is, filename).parse();
            FileContext fileCtx = new FileContext(this, filename);
            prg.execute(fileCtx);
        } catch (ParseException pex) {
            throw new RockstarRuntimeException("Error loading file " + filename + ", Parse error: " + pex.getMessage());
        }
    }
}
