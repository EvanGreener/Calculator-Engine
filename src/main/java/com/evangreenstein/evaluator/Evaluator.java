package com.evangreenstein.evaluator;

import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evangreenstein
 */
public class Evaluator {

    private final static Logger LOG = LoggerFactory.getLogger(Evaluator.class);

    public String evaluate(Queue<String> infixExpression) throws InvalidStringException, DivisionByZeroException {
        Queue<String> postfixQueue = new ArrayDeque<>();
        Deque<String> operatorStack = new ArrayDeque<>();
        
        // Infix to postfix conversion --- start

        while (!infixExpression.isEmpty()) {

            String string = infixExpression.peek();
            LOG.debug("String in front of infix queue: " + string);
            String operatorAtTop;
            if (isOperand(string)) {
                LOG.debug("Operand");
                postfixQueue.add(infixExpression.poll());
            } else if (isOperator(string)) {
                LOG.debug("Operator");
                operatorAtTop = operatorStack.peek();

                if (operatorAtTop != null) {
                    //Checking if 'string' has greater precdence than the opeator at the top of the stack
                    while (comparePrecedence(string, operatorAtTop) <= 0) {
                        postfixQueue.add(operatorStack.pop());
                        operatorAtTop = operatorStack.peek();
                    }
                }
                operatorStack.push(infixExpression.poll());

            } else {
                throw new InvalidStringException("The string is neither an operator or an operand");
            }
        }
        

        while (!operatorStack.isEmpty()) {
            postfixQueue.add(operatorStack.poll());
        }
        
        // Infix to postfix conversion --- end

        // Postfix evaluation --- start

        Deque<String> operandStack = new ArrayDeque<>();
        String[] expressionArray = new String[3];

        while (!postfixQueue.isEmpty()) {
            String string = postfixQueue.peek();
            if (isOperand(string)) {
                operandStack.push(postfixQueue.poll());
            } else if (isOperator(string)) {
                expressionArray[2] = operandStack.poll();
                expressionArray[1] = postfixQueue.poll();
                expressionArray[0] = operandStack.poll();
                operandStack.push(solveInfixExp(expressionArray[0], expressionArray[1], expressionArray[2]));
            } else {
                throw new InvalidStringException("The string is neither an operator or an operand");
            }
        }
        // Postfix evaluation --- end
        return operandStack.peek();
    }

    private boolean isOperand(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String value) {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/");
    }

    private int comparePrecedence(String op1, String op2) {

        return getPrecedence(op1) - getPrecedence(op2);
    }

    private int getPrecedence(String op) {
        if (op.equals("*") || op.equals("/")) {
            return 2;
        } else {
            return 1;
        }
    }

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
                if (op2 == 0){
                    throw new DivisionByZeroException("Division by 0 is an impossible operation");
                }
                result = (op1 / op2) + "";
                break;
        }

        return result;
    }
}
