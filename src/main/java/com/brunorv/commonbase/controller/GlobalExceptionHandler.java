package com.brunorv.commonbase.controller;


import com.brunorv.commonbase.exception.BussinesExceptionResponse;
import com.brunorv.commonbase.exception.BussinesRuleException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            String[] errorArray = ex.getMessage().split(", ");
            for (String error : errorArray) {
                String[] dataError = error.split(": ");
                errorResponse.put(dataError[0], dataError[1]);
            }
            return ResponseEntity.badRequest().body(errorResponse);

    }
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            String errorMessage = "Invalid value for field '" + fieldName + "'. Please provide a right value.";

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);

            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( ex.getMessage());
    }


    @ExceptionHandler({HttpClientErrorException.Conflict.class})
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.CONFLICT) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Conflict in the request. Please check your request data.");

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        return ResponseEntity.status(ex.getStatusCode()).build();
    }


    @ExceptionHandler(BussinesRuleException.class)
    public ResponseEntity<Object> handleBussinesRuleException(BussinesRuleException ex) {
        BussinesExceptionResponse errorResponse = new BussinesExceptionResponse(
                ex.getId(),
                ex.getCode(),
                ex.getMessage(),
                ex.getHttpStatus().value()
        );

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, Object> errorResponse = new HashMap<>();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errorResponse.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }

}