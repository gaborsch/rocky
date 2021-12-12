/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.List;

/**
 *
 * @author Gabor
 */
public interface ASTAware {

    public String getASTNodeText();

    public List<ASTAware> getASTChildren();

    public Integer getASTLineNum();

}
