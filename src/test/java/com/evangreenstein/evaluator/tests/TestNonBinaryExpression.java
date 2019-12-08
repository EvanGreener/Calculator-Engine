/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.tests;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.NonBinaryExpressionException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestNonBinaryExpression {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            
            {createExp15(), ""},
            {createExp20(), ""},
            {createExp21(), ""},
            

        });
    }
    
    private final Evaluator evaluator;
    private final Queue<String> expression;
    private final String expectedResult;
    
    /**
     *
     * @param expression
     * @param expectedResult
     */
    public TestNonBinaryExpression(Queue<String> expression, String expectedResult) {
        this.evaluator = new Evaluator();

        this.expression = expression;
        this.expectedResult = expectedResult;
        
    }
    @Test(expected = NonBinaryExpressionException.class, timeout = 100)
    public void testNonBinaryExpression() {
        
        evaluator.evaluate(expression);
        
    }
    
    /**
     * '4+5-'
     * Expected: NonBinaryExpressionException
     *
     * @return
     */
    private static Object createExp15() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("4");
        expression.add("+");
        expression.add("5");
        expression.add("-");

        return expression;
    }
    
    /**
     * '(/5(3+2))'
     * Expected: NonBinaryExpressionException
     * 
     * There can't be an operator after a '('
     * 
     * @return 
     */
    private static Object createExp20() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("/");
        expression.add("5");
        expression.add("(");
        expression.add("3");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add(")");

        return expression;

    }

    /**
     * '(3+2*)'
     * Expected: NonBinaryExpressionException
     * 
     * There can't be an operator before a ')'
     * 
     * @return 
     */
    private static Object createExp21() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("3");
        expression.add("+");
        expression.add("2");
        expression.add("*");
        expression.add(")");

        return expression;
    }

}
