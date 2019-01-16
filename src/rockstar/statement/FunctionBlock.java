/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class FunctionBlock extends Block {
    
    private String name;
    private List<String> parameterNames = new ArrayList<>();

    public FunctionBlock(String name) {
        this.name = name;
    }

    void addParameterName(String paramName){
        parameterNames.add(paramName);
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }
    
}
