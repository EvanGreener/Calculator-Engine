/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.tests;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestInvalidString {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {createExp11(),""},
            {createExp12(), ""},
            {createExp13(), ""}
            

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
    public TestInvalidString(Queue<String> expression, String expectedResult) {
        this.evaluator = new Evaluator();

        this.expression = expression;
        this.expectedResult = expectedResult;
        
    }
    
    @Test(expected = InvalidStringException.class, timeout = 300)
    public void testInvalidString() {
        
        evaluator.evaluate(expression);
        
    }
    
        /**
     * Expected: InvalidStringException
     * 
     * Not parsable 
     * 
     * @return
     */
    private static Object createExp11() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("9/3");

        return expression;
    }

    /**
     * Expected: InvalidStringException
     * 
     * Operand after an operand 
     *
     * @return
     */
    private static Object createExp12() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("3");
        expression.add("3");
        expression.add("+");

        return expression;
    }

    /**
     * Expected: InvalidStringException
     * 
     * Operator after an operator 
     *
     * @return
     */
    private static Object createExp13() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("*");
        expression.add("-");
        expression.add("4");

        return expression;
    }

    
}
