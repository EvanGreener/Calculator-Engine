/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.tests;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestDivisionByZero {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {createExp14(), ""}
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
    public TestDivisionByZero(Queue<String> expression, String expectedResult) {
        this.evaluator = new Evaluator();

        this.expression = expression;
        this.expectedResult = expectedResult;
        
    }
    
    @Test(expected = DivisionByZeroException.class, timeout = 100)
    public void testDivisionByZero() {
        evaluator.evaluate(expression);

    }
    
    /**
     * '76/(2-2)'
     * Expected: DivisionByZeroException
     *
     * @return
     */
    private static Object createExp14() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("76");
        expression.add("/");
        expression.add("(");
        expression.add("2");
        expression.add("-");
        expression.add("2");
        expression.add(")");

        return expression;
    }
    
}
