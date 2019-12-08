/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.tests;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.MismatchedParenthesisException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMismatchedParenthesis {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {createExp16(), ""},
            {createExp17(), ""},
            {createExp18(), ""}
            
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
    public TestMismatchedParenthesis(Queue<String> expression, String expectedResult) {
        this.evaluator = new Evaluator();

        this.expression = expression;
        this.expectedResult = expectedResult;
        
    }
    @Test(expected = MismatchedParenthesisException.class, timeout = 100)
    public void testNonMatchingParenthesis() {
        
        evaluator.evaluate(expression);
        
    }
    
    /**
     * '(4(5)3(2)'
     * Expected: NonMatchingParenthesisException
     *
     * @return
     */
    private static Object createExp16() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("4");
        expression.add("(");
        expression.add("5");
        expression.add(")");
        expression.add("3");
        expression.add("(");
        expression.add("2");
        expression.add(")");

        return expression;
    }

    /**
     * ')(4)5(3)(2)('
     * Expected: NonMatchingParenthesisException
     * 
     * Number of open and closed parenthesis match but two of them don't have a
     * matching one 
     * 
     * @return
     */
    private static Object createExp17() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add(")");
        expression.add("(");
        expression.add("4");
        expression.add(")");
        expression.add("5");
        expression.add("(");
        expression.add("3");
        expression.add(")");
        expression.add("(");
        expression.add("2");
        expression.add(")");
        expression.add("(");

        return expression;
    }

    /**
     * ')4(5(3)(2))'
     * Expected: NonMatchingParenthesisException
     * 
     * @return 
     */
    private static Object createExp18() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add(")");
        expression.add("4");
        expression.add("(");
        expression.add("5");
        expression.add("(");
        expression.add("3");
        expression.add(")");
        expression.add("(");
        expression.add("2");
        expression.add(")");
        expression.add(")");

        return expression;
    }

}
