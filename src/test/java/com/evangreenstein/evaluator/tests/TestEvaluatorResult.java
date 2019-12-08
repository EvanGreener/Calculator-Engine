package com.evangreenstein.evaluator.tests;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import com.evangreenstein.evaluator.exceptions.NonBinaryExpressionException;
import com.evangreenstein.evaluator.exceptions.MismatchedParenthesisException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class TestEvaluatorResult {
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {createExp1(), "2"},
            {createExp2(), "7"},
            {createExp3(), "1"},
            {createExp4(), "9"},
            {createExp5(), "12"},
            {createExp6(), "30"},
            {createExp7(), "2496"},
            {createExp8(), "51"},
            {createExp9(), "-98"},
            {createExp10(), "21"},
            {createExp19(), "55"},
            
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
    public TestEvaluatorResult(Queue<String> expression, String expectedResult) {
        this.evaluator = new Evaluator();

        this.expression = expression;
        this.expectedResult = expectedResult;
        
    }

    @Test(timeout = 100)
    public void testResult() {
        try {
            assertEquals(expectedResult, evaluator.evaluate(expression));
        } catch (DivisionByZeroException | InvalidStringException | NonBinaryExpressionException | MismatchedParenthesisException ex) {
            fail("Should not be throwing any exceptions");
        }
        
    }
    
    

    /**
     * '1+1'
     * Expected: 2
     * 
     * Tests if basic addition, subtraction, multiplication division. Assumes
     * that if one of them works then they all work in general
     *
     * @return
     */
    private static Object createExp1() {
        Queue<String> expression = new ArrayDeque<>();
        expression.add("1");
        expression.add("+");
        expression.add("1");

        return expression;
    }

    /**
     * '1+2*3'
     * Expected: 7
     * 
     * Tests order of operations. Again assumes that if this one test works then
     * order of operations works in general
     *
     * @return
     */
    private static Object createExp2() {
        Queue<String> expression = new ArrayDeque<>();
        expression.add("1");
        expression.add("+");
        expression.add("2");
        expression.add("*");
        expression.add("3");

        return expression;
    }

    /**
     * '(1+2)/3'
     * Expected: 1
     * 
     * Tests order of operations WITH parenthesis
     *
     * @return
     */
    private static Object createExp3() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("1");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add("/");
        expression.add("3");

        return expression;
    }

    /**
     * '3(1+2)'
     * Expected: 9
     * 
     * Tests implied multiplication on with the number of the left side
     *
     * @return
     */
    private static Object createExp4() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("3");
        expression.add("(");
        expression.add("1");
        expression.add("+");
        expression.add("2");
        expression.add(")");

        return expression;
    }

    /**
     * '(4+2)(3-1)'
     * Expected: 12
     * 
     * Tests implied multiplication with two sub-expressions
     *
     * @return
     */
    private static Object createExp5() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("4");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add("(");
        expression.add("3");
        expression.add("-");
        expression.add("1");
        expression.add(")");

        return expression;
    }

    /**
     * '(4+2)5'
     * Expected: 30
     * 
     * Tests implied multiplication with the number on the right side
     *
     * @return
     */
    private static Object createExp6() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("4");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add("5");

        return expression;
    }

    /**
     * '3(2+2)4(5+3)(9+4)2'
     * Expected: 2496
     * 
     * Tests a chain of implied multiplications
     *
     * @return
     */
    private static Object createExp7() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("3");
        expression.add("(");
        expression.add("2");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add("4");
        expression.add("(");
        expression.add("5");
        expression.add("+");
        expression.add("3");
        expression.add(")");
        expression.add("(");
        expression.add("9");
        expression.add("+");
        expression.add("4");
        expression.add(")");
        expression.add("2");

        return expression;
    }

    /**
     * '3(2+2)4+(1+2)'
     * Expected: 51
     * 
     * Tests implied multiplication combined with normal operations
     *
     * @return
     */
    private static Object createExp8() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("3");
        expression.add("(");
        expression.add("2");
        expression.add("+");
        expression.add("2");
        expression.add(")");
        expression.add("4");
        expression.add("+");
        expression.add("(");
        expression.add("1");
        expression.add("+");
        expression.add("2");
        expression.add(")");

        return expression;
    }

    /**
     * '-98'
     * Expected: -98
     * 
     * Edge case
     *
     * @return
     */
    private static Object createExp9() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("-98");

        return expression;
    }

    /**
     * '(21)'
     * Expected: 21
     * 
     * Another edge case
     *
     * @return
     */
    private static Object createExp10() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("21");
        expression.add(")");

        return expression;
    }


    

    
    /**
     * '(5(3+(2(9-5))))'
     * Expected: 55
     * 
     * Testing expressions within expressions
     * @return 
     */
    private static Object createExp19() {
        Queue<String> expression = new ArrayDeque<>();

        expression.add("(");
        expression.add("5");
        expression.add("(");
        expression.add("3");
        expression.add("+");
        expression.add("(");
        expression.add("2");
        expression.add("(");
        expression.add("9");
        expression.add("-");
        expression.add("5");
        expression.add(")");
        expression.add(")");
        expression.add(")");
        expression.add(")");


        return expression;
    }

    

}
