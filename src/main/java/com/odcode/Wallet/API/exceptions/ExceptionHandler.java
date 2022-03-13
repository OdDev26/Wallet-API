package com.odcode.Wallet.API.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<TransactionExceptionPayLoad> handleException(TransactionFailedException transactionFailedException ){
        TransactionExceptionPayLoad transactionExceptionPayLoad= new TransactionExceptionPayLoad();
        transactionExceptionPayLoad.setMessage(transactionFailedException.getMessage());
        return new ResponseEntity<>(transactionExceptionPayLoad, HttpStatus.BAD_REQUEST);
    }
}
