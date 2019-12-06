package com.evangreenstein.evaluator;

import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import com.evangreenstein.evaluator.exceptions.NonBinaryExpressionException;
import com.evangreenstein.evaluator.exceptions.NonMatchingParenthesisException;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basically a calculator which solves the expression given as input. The
 * evaluator however is only limited to addition, subtraction, multiplication
 * (with parenthesis as well) and division operations. No exponents, rooting,
 * trig functions etc.
 *
 */
public class Evaluator {

    private final static Logger LOG = LoggerFactory.getLogger(Evaluator.class);

    /**
     * The workhorse of this class.It evaluates a given infix expression which
     * is represented by a queue of strings. It first performs the infix to
     * postfix conversion and then evaluates the postfix expression.
     *
     * @param infixExpression
     * @return the result of the expression
     * @throws InvalidStringException
     * @throws DivisionByZeroException
     * @throws NonBinaryExpressionException
     */
    public String evaluate(Queue<String> infixExpression) throws InvalidStringException, DivisionByZeroException, NonBinaryExpressionException, NonMatchingParenthesisException {
        Queue<String> postfixQueue = new ArrayDeque<>();
        Deque<String> operatorStack = new ArrayDeque<>();
        LOG.info("Infix expression: " + infixExpression.toString());

        if (compareParathesis(infixExpression) > 0) {
            throw new NonMatchingParenthesisException("At least one mismatched '(' ");
        } else if (compareParathesis(infixExpression) < 0) {
            throw new NonMatchingParenthesisException("At least one mismatched ')' ");
        }
        // Infix to postfix conversion --- start

        while (!infixExpression.isEmpty()) {
            String string = infixExpression.peek();
            //LOG.debug("String in front of infix queue: " + string);

            String operatorAtTop;
            if (isOpenParenthesis(string)) {
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
                        //LOG.debug(value);
                        if (isOpenParenthesis(value)) {
                            //LOG.debug("openParenthesisCount++;");
                            openParenthesisCount++;
                        } else if (isClosedParenthesis(value)) {
                            //LOG.debug("closedParenthesisCount++;");
                            closedParenthesisCount++;
                            //To exclude the last parenthesis in sub-expression
                            if (openParenthesisCount == closedParenthesisCount) {
                                continue;
                            }
                        }
                        //LOG.debug(openParenthesisCount + " " + closedParenthesisCount );
                        subExpression.add(infixExpression.poll());
                    }
                } catch (NullPointerException ex) {
                    throw new NonMatchingParenthesisException("Mismatched '('", ex);
                }
                //It is impossible for the value before a ')' to be an operator 
                if (isOperator(infixExpression.peek())) {
                    throw new NonBinaryExpressionException("There cannot be an operator right before a ')'");
                }
                //LOG.debug("sub expression: "+ subExpression.toString());
                postfixQueue.add(evaluate(subExpression));
                //To remove the last parenthesis in the expression
                infixExpression.poll();
            } else if (isOperand(string)) {
                LOG.debug("Operand");
                postfixQueue.add(infixExpression.poll());
            } else if (isOperator(string)) {
                LOG.debug("Operator");
                operatorAtTop = operatorStack.peek();
                //LOG.debug("     string = '" + string + "' , opAtTop = '" + operatorAtTop + "'");
                //Checking if there's something in the stack
                if (operatorAtTop != null) {

                    //Checking if 'string' has greater precdence than whatever operator is
                    //currently at the top of the stack
                    while (comparePrecedence(string, operatorAtTop) <= 0) {
                        postfixQueue.add(operatorStack.pop());
                        if (!operatorStack.isEmpty()) {
                            operatorAtTop = operatorStack.peek();
                        } else {
                            break;
                        }
                    }
                }
                operatorStack.push(infixExpression.poll());

            } else {
                if (isClosedParenthesis(string)) {
                    throw new InvalidStringException("An expression cannot start with a ')'");
                }
                throw new InvalidStringException("The string is neither an operator or an operand");
            }
        }

        emptyOperatorStack(operatorStack, postfixQueue);
        // Infix to postfix conversion --- end
        // Postfix evaluation --- start

        LOG.info("Postfix expression: " + postfixQueue.toString());

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

    /**
     *
     * @param value
     * @return whether or not 'value' is one of the operators
     */
    private boolean isOperator(String value) {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/");
    }

    private boolean isOpenParenthesis(String value) {
        //LOG.debug("[isOpenParenthesis] " + value);
        return value.equals("(");
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
     * Solves an infix expression given the operands and the operator
     *
     * @param operand1
     * @param operator
     * @param operand2
     * @return the result
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

    private void emptyOperatorStack(Deque<String> operatorStack, Queue<String> postfixQueue) {
        while (!operatorStack.isEmpty()) {
            postfixQueue.add(operatorStack.poll());
        }
    }

    /**
     * Was used for testing while this class was in development
     *
     * @return the expression
     */
    public Queue<String> createExpression() {
        Queue<String> exp = new ArrayDeque<>();
        exp.add("(");
        exp.add("2");
        exp.add("+");
        exp.add("4");
        exp.add("/");
        exp.add("2");
        exp.add(")");
        return exp;
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

}
