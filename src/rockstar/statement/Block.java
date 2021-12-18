/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import rockstar.parser.Keyword;
import rockstar.parser.Line;
import rockstar.parser.ParseException;
import rockstar.runtime.ASTAware;
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
    private final Map<String, List<String>> listCache = new HashMap<>();

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

    protected void defineAlias(String keywords, String alias) {
        aliasesFor
                .computeIfAbsent(Arrays.asList(keywords.split(" ")), l -> new ArrayList<>())
                .add(Arrays.asList(alias.split(" ")));
    }

    public void defineAlias(List<String> keywords, List<String> alias) {
        List<String> lcAlias = alias.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> lcKeywords = keywords.stream().map(String::toLowerCase).collect(Collectors.toList());

        aliasesFor
                .computeIfAbsent(lcKeywords, l -> new ArrayList<>())
                .add(lcAlias);
    }

    private List<String> listOf(String s) {
        return listCache.computeIfAbsent(s, v -> Arrays.asList(v));
    }

    public List<List<String>> getAliasesFor(Keyword kw) {
        List<List<String>> aliases = new LinkedList<>();
        for (String variation : kw.getKeywordVariations()) {
            aliases.add(listOf(variation));
            aliases.addAll(getAliasesLC(listOf(variation)));
        }
        return aliases;
    }

    public List<List<String>> getAliasesFor(List<String> keywords) {
        List<String> lcKeywords = keywords.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<List<String>> aliases = getAliasesLC(lcKeywords);
        if (aliases.isEmpty()) {
            aliases.add(lcKeywords);
        }
        return aliases;
    }

    protected List<List<String>> getAliasesLC(List<String> lcKeywords) {
        List<List<String>> aliases;
        if (parent != null) {
            aliases = parent.getAliasesLC(lcKeywords);
        } else {
            aliases = new ArrayList<>();
        }
        List<List<String>> localAliases = aliasesFor.get(lcKeywords);
        if (localAliases != null) {
            aliases.addAll(localAliases);
        }
        return aliases;
    }

    public Iterator<Map.Entry<List<String>, List<List<String>>>> getAliasesIterator() {
        return aliasesFor.entrySet().iterator();
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

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astValues = new ArrayList<>(statements.size());
        astValues.addAll(statements);
        return astValues;
    }

}
