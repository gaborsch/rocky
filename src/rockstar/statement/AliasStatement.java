/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import rockstar.runtime.BlockContext;
import rockstar.runtime.FileContext;
import rockstar.runtime.PackagePath;
import rockstar.runtime.ProgramContext;
import rockstar.runtime.QualifiedClassName;

/**
 *
 * @author Gabor
 */
public class AliasStatement extends Statement {

    private List<String> alias;
    private List<String> keyword;

    public AliasStatement(List<String> alias, List<String> keyword) {
        this.alias = alias;
        this.keyword = keyword;
    }

    public List<String> getAlias() {
        return alias;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    @Override
    public void execute(BlockContext ctx) {
        // alias definition executed immediately by the Parser
    }

    @Override
    protected String explain() {        
        return alias.stream().collect(Collectors.joining(" "))
            + " means "
            + keyword.stream().collect(Collectors.joining(" "));
    }

}
