/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class FunctionBlock extends Block {

    private String name;
    private List<String> parameterNames = new ArrayList<>();
    private boolean hasReturn = false;

    public FunctionBlock(String name) {
        this.name = name;
    }

    void addParameterName(String paramName) {
        parameterNames.add(paramName);
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public void addStatement(Statement stmt) {
        super.addStatement(stmt);
        hasReturn |= (stmt instanceof ReturnStatement);
    }

    @Override
    public boolean blockClosed() {
        // a function block is closeable properly if it has at least one return statement
//        return hasReturn;
        return true;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    FUNCDEF: " + name + "(" + Arrays.deepToString(parameterNames.toArray()) + ")";
    }
}
