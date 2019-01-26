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

    private final String name;
    private final List<String> parameterNames = new ArrayList<>();

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
    public String toString() {
        return super.toString()
                + "\n    FUNCDEF: " + name + "(" + Arrays.deepToString(parameterNames.toArray()) + ")";
    }
}
