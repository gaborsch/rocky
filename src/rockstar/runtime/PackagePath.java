/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;

/**
 *
 * @author Gabor
 */
public class PackagePath {

    public static final PackagePath DEFAULT = new PackagePath(new LinkedList<>());
    
    private final List<String> path;

    /**
     * Package path is created through the factory method only
     * @param path 
     */
    private PackagePath(List<String> path) {
        this.path = path;
    }

    public static Optional<PackagePath> getPackagetPathFromExpr(Expression expr) {
        List<String> path = processPathExpr(expr, new LinkedList<>());
        if (path == null) {
            return Optional.empty();
        }
        return Optional.of(new PackagePath(path));
    }

    private static List<String> processPathExpr(Expression expr, List<String> path) {
        if (expr instanceof ListExpression) {
            // list expressions are porcessed one by one
            ListExpression le = (ListExpression) expr;
            for (Expression part : le.getParameters()) {
                path = processPathExpr(part, path);
                if (path == null) {
                    return null;
                }
            }
        } else if (expr instanceof VariableReference) {
            // Variable names are taken characterwise
            VariableReference vr = (VariableReference) expr;
            String name = formatVariableName(vr.getName());
            path.add(name);
            return path;
        } else if (expr instanceof ConstantExpression) {
            // String constants are parsed for slashes
            ConstantExpression ce = (ConstantExpression) expr;
            if (ce.getValue().isString()) {
                String value = ce.getValue().getString();
                String[] parts = value.split("/");
                for (String part : parts) {
                    // all parts are added one by one, empty parts skipped
                    String partFormatted = formatVariableName(part);
                    if (!partFormatted.isEmpty()) {
                        path.add(partFormatted);
                    }
                }
            }

        } else {
            // other expressions like CompoundExpression, ErrorExpressions are not allowed
            return null;
        }
        return path;
    }

    private static String formatVariableName(String name) {
        return name.replaceAll("[^a-zA-Z]+", "_");
    }

    public List<String> getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String part : path) {
            sb.append(sb.length() > 0 ? "/" : "").append(part);
        }
        return sb.toString();
    }

}
