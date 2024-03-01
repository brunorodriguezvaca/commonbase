package com.brunorv.commonbase.exception;

public class BussinesExceptionResponse {
    private long id;
    private String code;
    private String message;
    private int status;

    public BussinesExceptionResponse(long id, String code, String message, int value) {
        this.id=id;
        this.code=code;
        this.message=message;
        this.status=value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
