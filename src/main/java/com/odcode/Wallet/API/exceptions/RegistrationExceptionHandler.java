package com.odcode.Wallet.API.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegistrationExceptionHandler {
    @ExceptionHandler(value = {RegistrationFailedException.class})
    public ResponseEntity<RegistrationExceptionPayload> handleRegistrationExceptions(RegistrationFailedException registrationFailedException){
        RegistrationExceptionPayload registrationExceptionPayload= new RegistrationExceptionPayload();
        registrationExceptionPayload.setMessage(registrationFailedException.getMessage());
        return new ResponseEntity<>(registrationExceptionPayload, HttpStatus.BAD_REQUEST);
    }
}
