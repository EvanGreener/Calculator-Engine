/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.main;

import com.evangreenstein.evaluator.Evaluator;
import com.evangreenstein.evaluator.exceptions.DivisionByZeroException;
import com.evangreenstein.evaluator.exceptions.InvalidStringException;
import java.util.ArrayDeque;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evangreenstein
 */
public class Main {
    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InvalidStringException, DivisionByZeroException{
        Evaluator eval = new Evaluator();
        Queue<String> exp = new ArrayDeque<>();
        exp.add("2");
        exp.add("+");
        exp.add("4");
        exp.add("*");
        exp.add("3");
        exp.add("/");
        exp.add("3");
        
        LOG.info("\n Result: " + eval.evaluate(exp));
        
    }
}
