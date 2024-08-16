package com.brunorv.commonbase.controller;


import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.brunorv.commonbase.exception.BussinesExceptionResponse;
import com.brunorv.commonbase.exception.BussinesRuleException;
import com.brunorv.commonbase.exception.InvalidFormException;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({SignatureGenerationException.class})
    public ResponseEntity<Object> handleInvalidTokenExceptions(SignatureGenerationException ex) {
        BussinesExceptionResponse bussinesExceptionResponse = new BussinesExceptionResponse(
                "INVALID-TOKEN",
                ex.getMessage(),
                null,
                ex.getStackTrace()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(bussinesExceptionResponse);
    }

    @ExceptionHandler({InvalidFormException.class})
    public ResponseEntity<Object> handleInvalidFormExceptions(InvalidFormException ex) {
        BussinesExceptionResponse bussinesExceptionResponse = new BussinesExceptionResponse(
                "INVALID-FORM",
                ex.getMessage(),
                ex.getErrorResponse(),
                ex.getStackTrace()
        );
        return ResponseEntity.badRequest().body(bussinesExceptionResponse);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(ConstraintViolationException ex) {
            Map<String, Object> errorResponse = new HashMap<>();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            String fullPath = violation.getPropertyPath().toString();
            String fieldName = fullPath.substring(fullPath.lastIndexOf('.') + 1);
            errorResponse.put(fieldName, violation.getMessage());
        }

        BussinesExceptionResponse bussinesExceptionResponse = new BussinesExceptionResponse(
                "INVALID-FORM",
                ex.getMessage(),
                errorResponse,
                ex.getStackTrace()
        );

            return ResponseEntity.badRequest().body(bussinesExceptionResponse);

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