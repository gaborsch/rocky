/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

/**
 *
 * @author Gabor
 */
public class ParamList {

    /**
     * @return the params
     */
    public Object[] getParams() {
        return params;
    }
    
    private Object[] params;

    public ParamList(Object... params) {
        this.params = params;
    }
    
}
