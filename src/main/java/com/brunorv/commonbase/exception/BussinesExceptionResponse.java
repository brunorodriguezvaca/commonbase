package com.brunorv.commonbase.exception;

import java.util.Map;

public class BussinesExceptionResponse {
    private String code;
    private String message;
    Map<String, Object> chainOfErrors;
    StackTraceElement[] stackTraceElements;

    public BussinesExceptionResponse(String code, String message, Map<String, Object> chainErrors) {
        this.code=code;
        this.message=message;
        this.chainOfErrors =chainErrors;
    }

    public BussinesExceptionResponse(String code, String message, Map<String, Object> chainErrors,StackTraceElement[] stackTraceElements) {
        this.code=code;
        this.message=message;
        this.chainOfErrors =chainErrors;
        this.stackTraceElements=stackTraceElements;
    }

    public BussinesExceptionResponse(long id, String code, String message, int value) {
        this.code=code;
        this.message=message;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public Map<String, Object> getChainOfErrors() {
        return chainOfErrors;
    }

    public void setChainOfErrors(Map<String, Object> chainOfErrors) {
        this.chainOfErrors = chainOfErrors;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
