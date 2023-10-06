/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import static rockstar.parser.checker.Checker.PlaceholderType.LITERAL_OR_VARIABLE_OR_LIST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.Value;
import rockstar.statement.ClassBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ClassBlockChecker extends Checker<VariableReference, Expression, Expression> {

    private static final List<String> LOOK_LIKE = Arrays.asList("look", "like");
    private static final List<String> LOOKS_LIKE = Arrays.asList("looks", "like");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
  		new ParamList(variableAt(1), LOOK_LIKE, LITERAL_OR_VARIABLE_OR_LIST.at(2)),
        new ParamList(variableAt(1), LOOKS_LIKE, LITERAL_OR_VARIABLE_OR_LIST.at(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference nameRef = getE1();
        Expression parentsRef = getE2();
        
        List<Expression> parentExprList = new ArrayList<>();
        if (parentsRef instanceof ConstantExpression || parentsRef instanceof VariableReference) {
        	parentExprList.add(parentsRef);
        } else if (parentsRef instanceof ListExpression) {
        	parentExprList.addAll(((ListExpression)parentsRef).getParameters());
        } else {
			// only valid interface names are allowed
			return null;
		}

        String className = nameRef.getName();
        String parentName = null; 
        List<String> interfaceNames = new ArrayList<>();
        
        boolean isFirst = true;
    	for (Expression parentNameExpression : parentExprList) {
    		if (isFirst && parentNameExpression instanceof ConstantExpression) {
                Value v = ((ConstantExpression) parentNameExpression).getValue();
                // checking for "nothing" and its aliases
                if (v.isNull()) {
                	interfaceNames.add(null);
               	} else {;
                	// no other constant name is valid
                	return null;
                }
				;
			} else if (parentNameExpression instanceof VariableReference) {
				interfaceNames.add(((VariableReference) parentNameExpression).getName());
			} else {
				// only valid interface names are allowed
				return null;
			}
    		isFirst = false;
		}
    	// the first name will be the parent class name
    	parentName = interfaceNames.remove(0);
        
    	return new ClassBlock(className, parentName, interfaceNames);
    }
    
}
