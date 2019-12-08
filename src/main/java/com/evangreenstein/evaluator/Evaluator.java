/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator;

import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import com.evangreenstein.evaluator.exceptions.NonBinaryExpressionException;
import com.evangreenstein.evaluator.exceptions.MismatchedParenthesisException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evangreenstein
 */
public class Evaluator {

    private final static Logger LOG = LoggerFactory.getLogger(Evaluator.class);

    //Since it's mutable, there's no need to create more than one array for each instance of an Evaluator
    private final String[] expressionArray = new String[3];

    public String evaluate(Queue<String> infixExpression) throws InvalidStringException, DivisionByZeroException, NonBinaryExpressionException, MismatchedParenthesisException {

        Objects.requireNonNull(infixExpression);
        LOG.info("Infix expression: " + infixExpression.toString());
        if (compareParathesis(infixExpression) > 0) {
            throw new MismatchedParenthesisException("At least one mismatched '(' ");
        } else if (compareParathesis(infixExpression) < 0) {
            throw new MismatchedParenthesisException("At least one mismatched ')' ");
        }

        String result = evaluateExpression(infixExpression);
        LOG.info("Final Result = "+result);
        return result;

    }

    private String evaluateExpression(Queue<String> infixExpression) throws InvalidStringException, NonBinaryExpressionException, MismatchedParenthesisException, DivisionByZeroException {
        Queue<String> postfixExpression = new ArrayDeque<>();
        convertToPostfix(infixExpression, postfixExpression);
        return evaluatePostfix(postfixExpression);

    }

    private void convertToPostfix(Queue<String> infixExpression, Queue<String> postfixExpression) throws InvalidStringException, NonBinaryExpressionException, MismatchedParenthesisException, DivisionByZeroException {
        LOG.debug("[convertToPostfix] - Infix expression: " + infixExpression.toString());
        Deque<String> operatorStack = new ArrayDeque<>();
        boolean wasOperand = false;
        boolean wasOperator = false;

        while (!infixExpression.isEmpty()) {
            LOG.debug("Postfix expression: " + postfixExpression.toString());
            String value = infixExpression.peek();
            String operatorOnTop;

            if (isOpenParenthesis(value)) {
                String result = evaluteSubExpression(infixExpression, postfixExpression);
                LOG.debug("Result: "+result);
                result = performImpliedMultiplication(infixExpression, postfixExpression , result);
                LOG.debug(result);
                postfixExpression.add(result);
                LOG.debug("[performImpliedMultiplication] Postfix expression: " + postfixExpression.toString());

            } else if (isOperand(value)) {
                LOG.debug("Operand");
                if (wasOperand) {
                    throw new InvalidStringException("An operand cannot follow another operand unless it's "
                            + "a sub-expression (parentesis follow it)");
                }
                wasOperand = true;
                wasOperator = false;
                
                String result = infixExpression.poll();
                postfixExpression.add(result);
                performImpliedMultiplication(infixExpression, postfixExpression, result);

            } else if (isOperator(value)) {
                if (wasOperator) {
                    throw new InvalidStringException("An operator cannot follow another operator");
                }
                wasOperand = false;
                wasOperator = true;
                
                
                LOG.debug("Operator");
                operatorOnTop = operatorStack.peek();
                LOG.debug("operator currently on top of the stack = "+operatorOnTop);
                //Checking if there's something in the stack
                if (operatorOnTop != null) {
                    //Checking if 'string' has greater precdence than whatever operator is
                    //currently at the top of the stack
                    while (comparePrecedence(value, operatorOnTop) <= 0) {
                        postfixExpression.add(operatorStack.pop());
                        if (!operatorStack.isEmpty()) {
                            operatorOnTop = operatorStack.peek();
                        } else {
                            break;
                        }
                    }
                }
                operatorStack.push(infixExpression.poll());
                
                try{
                    if (isClosedParenthesis(infixExpression.peek())){
                        throw new InvalidStringException("Cannot have a ')' after an operator");
                    } 
                }
                catch (NullPointerException e){
                    throw new NonBinaryExpressionException("An operator must have exactly two operands");
                }
                
                
            } else {
                if (isClosedParenthesis(value)) {
                    throw new MismatchedParenthesisException("An expression cannot start with a ')'");
                }
                throw new InvalidStringException("There is/are is an operation/s in this expression "
                        + "that is not supported by this evaluator");
            }
            
        }
        emptyOperatorStack(operatorStack, postfixExpression );

    }

    private String evaluatePostfix(Queue<String> postfixExpression) throws DivisionByZeroException, InvalidStringException {
        LOG.debug("evaluatePostfix - Postfix expression: " + postfixExpression.toString());
        Deque<String> operandStack = new ArrayDeque<>();

        while (!postfixExpression.isEmpty()) {
            String string = postfixExpression.peek();
            if (isOperand(string)) {
                operandStack.push(postfixExpression.poll());
            } else if (isOperator(string)) {
                expressionArray[2] = operandStack.poll();
                expressionArray[1] = postfixExpression.poll();
                expressionArray[0] = operandStack.poll();
                operandStack.push(solveInfixExp(expressionArray[0], expressionArray[1], expressionArray[2]));
            } else {
                throw new InvalidStringException("There is/are is an operation/s that not supported by this evaluator");
            }
        }
        LOG.debug("Final operand stack: "+ operandStack.toString());
        return operandStack.peek();    
    }

    /**
     * Evaluates a an expression in parenthesis
     * 
     * @param infixExpression
     * @param postfixExpression
     * @return the result
     * @throws InvalidStringException
     * @throws NonBinaryExpressionException
     * @throws MismatchedParenthesisException
     * @throws DivisionByZeroException 
     */
    private String evaluteSubExpression(Queue<String> infixExpression, Queue<String> postfixExpression) throws InvalidStringException, NonBinaryExpressionException, MismatchedParenthesisException, DivisionByZeroException {
        LOG.debug("[evaluteSubExpression] - Infix expression: " + infixExpression.toString());
        //To remove the first parenthesis in the expression
        infixExpression.poll();

        //It is impossible for the value after a '(' to be an operator
        if (isOperator(infixExpression.peek())) {
            throw new NonBinaryExpressionException("There cannot be an operator right after a '('");
        }

        Queue<String> subExpression = new ArrayDeque<>();
        int openParenthesisCount = 1; //The one that was just encountered
        int closedParenthesisCount = 0;
        //This should stop when a closed parenthesis is encountered. But in the
        //case there's a sub-sub-exprssion, it should also not stop until the 
        //number of open and closed parenthesis match
        try {
            for (String value = infixExpression.peek();
                    !isClosedParenthesis(value) || openParenthesisCount != closedParenthesisCount;
                    value = infixExpression.peek()) {
                LOG.debug(value);
                if (isOpenParenthesis(value)) {
                    LOG.debug("openParenthesisCount++;");
                    openParenthesisCount++;
                } else if (isClosedParenthesis(value)) {
                    LOG.debug("closedParenthesisCount++;");
                    closedParenthesisCount++;
                    //To exclude the last parenthesis in sub-expression
                    if (openParenthesisCount == closedParenthesisCount) {
                        continue;
                    }
                }
                LOG.debug(openParenthesisCount + " " + closedParenthesisCount);
                subExpression.add(infixExpression.poll());
            }
        } catch (NullPointerException ex) {
            //Still possible that the expression looks like this: ')()('
            throw new MismatchedParenthesisException("An expression cannot look like this: ')()(' ", ex);
        }
        //It is impossible for the value before a ')' to be an operator 
        if (isOperator(infixExpression.peek())) {
            throw new NonBinaryExpressionException("There cannot be an operator right before a ')'");
        }
        LOG.debug("sub expression: " + subExpression.toString());
        
        //To remove the last parenthesis in the expression
        infixExpression.poll();
        
        //Recursion!!
        return evaluateExpression(subExpression);
    }

    /**
     * Checks if there's implied multiplication and performs it
     * 
     * @param infixExpression
     * @param postfixExpression
     * @param result
     * @return returns the result of the multiplication(s)
     * @throws InvalidStringException
     * @throws NonBinaryExpressionException
     * @throws MismatchedParenthesisException
     * @throws DivisionByZeroException 
     */
    private String performImpliedMultiplication(Queue<String> infixExpression, Queue<String> postfixExpression, String result) throws InvalidStringException, NonBinaryExpressionException, MismatchedParenthesisException, DivisionByZeroException {
        String newValue = infixExpression.peek();
        LOG.debug("[performImpliedMultiplication] - newValue "+newValue);
        boolean wasParenthesis = false;
        try{
            //Checking for sub-expressions right next to it
            while (isOpenParenthesis(newValue)) {
                LOG.debug("[performImpliedMultiplication] - in while ");
                wasParenthesis = true;
                //evaluate sub expression and multiply it to whatever is in front of the postfix expression
                String result2 = evaluteSubExpression(infixExpression, postfixExpression);
                LOG.debug("performImpliedMultiplication result2 - " + result2);
                result = solveInfixExp(result, "*", result2);
                LOG.debug("performImpliedMultiplication result - " + result);
                newValue = infixExpression.peek();
            }
            //Multiplication on the right side of the brace
            if (isOperand(newValue) && wasParenthesis) {
                LOG.debug("[performImpliedMultiplication] - in if ");
                //multiply value to whatever is in front of the postfix expression
                result = solveInfixExp(result, "*", newValue);
            }  
            
        }
        catch (NullPointerException ex){
            //Reached the end of the expression
            return result;
            
        }
        return result;
        
    }
    
    /**
     * Checks whether the number of open and closed parenthesis match
     *
     * @param infixExpression
     * @return whether or not they match
     */
    private int compareParathesis(Queue<String> infixExpression) {
        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;

        for (String value : infixExpression) {
            if (isOpenParenthesis(value)) {
                openParenthesisCount++;
            } else if (isClosedParenthesis(value)) {
                closedParenthesisCount++;
            }
        }
        return openParenthesisCount - closedParenthesisCount;
    }
    
    /**
     * Puts the remaining elements in the operator stack in the postfix expression
     * 
     * @param operatorStack
     * @param postfixQueue 
     */
    private void emptyOperatorStack(Deque<String> operatorStack, Queue<String> postfixQueue) {
        while (!operatorStack.isEmpty()) {
            postfixQueue.add(operatorStack.poll());
        }
    }

    /**
     *
     * @param value
     * @return whether or not 'value' can be converted to an integer
     */
    private boolean isOperand(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOpenParenthesis(String value) {
        //LOG.debug("[isOpenParenthesis] " + value);
        return value.equals("(");
    }

    /**
     *
     * @param value
     * @return whether or not 'value' is one of the operators
     */
    private boolean isOperator(String value) {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/");
    }

    private boolean isClosedParenthesis(String value) {
        //LOG.debug("[isClosedParenthesis] " + value);
        return value.equals(")");
    }

    /**
     * Used to compare the precedence of two operators.
     *
     * @param op1
     * @param op2
     * @return >0 if 'op1' has higher precedence than 'op2', 0 if they're equal
     * and <0 if 'op1' has lower precedence than 'op2'
     */
    private int comparePrecedence(String op1, String op2) {
        LOG.debug("     op1 = '" + op1 + "' , op2 = '" + op2 + "'");
        return getPrecedence(op1) - getPrecedence(op2);
    }
    
    /**
     *
     * @param op
     * @return The precedence of the operator
     */
    private int getPrecedence(String op) {
        if (op.equals("*") || op.equals("/")) {
            return 2;
        } else {
            return 1;
        }
    }
    
    /**
     * Solves a simple expression
     * 
     * @param operand1
     * @param operator
     * @param operand2
     * @return The result
     * @throws DivisionByZeroException 
     */
    private String solveInfixExp(String operand1, String operator, String operand2) throws DivisionByZeroException {
        int op1 = Integer.parseInt(operand1);
        int op2 = Integer.parseInt(operand2);
        String result;

        switch (operator) {
            case "+":
                result = (op1 + op2) + "";
                break;
            case "-":
                result = (op1 - op2) + "";
                break;
            case "*":
                result = (op1 * op2) + "";
                break;
            default:
                if (op2 == 0) {
                    throw new DivisionByZeroException("Division by 0 is an impossible operation");
                }
                result = (op1 / op2) + "";
                break;
        }

        return result;
    }

}
