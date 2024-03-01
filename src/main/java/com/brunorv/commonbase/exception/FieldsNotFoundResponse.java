package com.brunorv.commonbase.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class FieldsNotFoundResponse {
    private String message;
    List<String> fieldsNotFound;

    public List<String> getFieldsNotFound() {
        return fieldsNotFound;
    }

    public void setFieldsNotFound(List<String> fieldsNotFound) {
        this.fieldsNotFound = fieldsNotFound;
    }

    public FieldsNotFoundResponse(String message, List<String> fieldsNotFound) {
        this.message = message;
        this.fieldsNotFound=fieldsNotFound;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJsonString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            // Manejar cualquier excepción de serialización aquí
            e.printStackTrace();
            return "{\"message\":\"Error al serializar el mensaje\"}";
        }
    }
}
