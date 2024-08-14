package com.brunorv.commonbase.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InvalidFormException extends Throwable {

    Map<String, Object> errorResponse;

    public InvalidFormException(Map<String, Object> errorResponse) {
      this.errorResponse=errorResponse;
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
