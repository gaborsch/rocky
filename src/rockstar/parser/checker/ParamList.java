/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gabor
 */
public class ParamList {

    private final static Map<String, List<String>> listCache = new HashMap<>();

    /**
     * @return the params
     */
    public Object[] getParams() {
        return params;
    }

    private Object[] params;

    public ParamList(Object... orig) {
        params = new Object[orig.length];
        for (int i = 0; i < orig.length; i++) {
            Object value = orig[i];
            params[i] = (value != null && value instanceof String)
                    ? listCache.computeIfAbsent((String) value, s -> Arrays.asList(s))
                    : value;
        }
    }

}
