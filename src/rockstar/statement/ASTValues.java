/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.runtime.ASTAware;

/**
 *
 * @author Gabor
 */
public class ASTValues implements ASTAware {

    private final String nodeText;

    public static List<ASTAware> of(String... params) {
        List<ASTAware> astParams = new ArrayList<>(params.length);
        for (String param : params) {
            astParams.add(new ASTValues(param));
        }
        return astParams;
    }

    public static List<ASTAware> of(ASTAware... params) {
        List<ASTAware> astParams = new ArrayList<>(params.length);
        for (ASTAware param : params) {
            if (param != null) {
                astParams.add(param);
            }
        }
        return astParams;
    }

    private ASTValues(String nodeText) {
        this.nodeText = nodeText;
    }

    @Override
    public String getASTNodeText() {
        return nodeText;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return null;
    }

    @Override
    public Integer getASTLineNum() {
        return null;
    }

}
