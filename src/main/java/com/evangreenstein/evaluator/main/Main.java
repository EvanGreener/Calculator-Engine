/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.main;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import com.evangreenstein.evaluator.exceptions.NonBinaryExpressionException;
import com.evangreenstein.evaluator.exceptions.NonMatchingParenthesisException;
import java.util.ArrayDeque;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Was used for testing the evaluator while it was in development
 */
public class Main {
    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InvalidStringException, DivisionByZeroException, NonBinaryExpressionException, NonMatchingParenthesisException{
        Evaluator eval = new Evaluator();
        Queue<String> exp = eval.createExpression();
        LOG.info("Result: " + eval.evaluate(exp));
        
    }

}
