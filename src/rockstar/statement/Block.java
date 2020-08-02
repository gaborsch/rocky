/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import rockstar.parser.Line;
import rockstar.parser.ParseException;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public abstract class Block extends Statement {

    private Block parent;

    private final List<Statement> statements = new ArrayList<>();

    private final Map<List<String>, List<List<String>>> aliasesFor = new HashMap<>();

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement stmt) {
        if (!stmt.applyTo(this)) {
            throw new ParseException(stmt.getClass().getSimpleName() + " cannot be applied to the block", stmt.getLine());
        }
        statements.add(stmt);
    }

    public Statement lastStatement() {
        if (statements.size() > 0) {
            statements.get(statements.size() - 1);
        }
        return null;
    }

    public void setParent(Block parent) {
        this.parent = parent;
    }

    public Block getParent() {
        return parent;
    }

    public void defineAlias(List<String> alias, List<String> keyword) {
        List<String> lcAlias = alias.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> lcKeywords = keyword.stream().map(String::toLowerCase).collect(Collectors.toList());
        
        List<List<String>> aliasList = aliasesFor.computeIfAbsent(lcKeywords, (l) -> new LinkedList<>());
        if (aliasList.isEmpty()) {
            aliasList.add(keyword);
        }
        aliasList.add(alias);
        
        aliasesFor
            .computeIfAbsent(lcKeywords, (l) -> new LinkedList<>())
            .add(lcAlias);
    }
    
    public List<List<String>> getAliasesFor(List<String> keyword) {
        List<List<String>> aliases = aliasesFor.getOrDefault(keyword, new ArrayList<>());
        if (aliases.isEmpty()) {
            aliases.add(keyword);
        }
        return aliases;
    }

    /**
     * Execute a block
     *
     * @param ctx
     */
    @Override
    public void execute(BlockContext ctx) {
        for (Statement statement : statements) {
            ctx.beforeStatement(statement);
            try {
                statement.execute(ctx);
            } catch (RockstarRuntimeException rre) {
                Line l = statement.getLine();
                rre.addStacktraceLine(l, ctx);
                throw rre;
            }            
        }
    }

}
