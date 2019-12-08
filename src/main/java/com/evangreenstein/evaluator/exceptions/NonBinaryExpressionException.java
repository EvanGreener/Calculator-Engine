/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evangreenstein.evaluator.exceptions;

/**
 *
 * @author evangreenstein
 */
public class NonBinaryExpressionException extends RuntimeException{
    public NonBinaryExpressionException(String errorMessage) {
        super(errorMessage);
    }
    
    public NonBinaryExpressionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
