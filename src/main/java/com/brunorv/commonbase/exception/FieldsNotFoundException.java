package com.brunorv.commonbase.exception;

import java.util.List;

public class FieldsNotFoundException extends Exception {

    private String message;
    List<String> fieldsNotFound;
    public FieldsNotFoundException() {
        super();
    }

    public FieldsNotFoundException(String mensaje, List<String> fieldsNotFound) {
        super(mensaje);
        this.message=mensaje;
        this.fieldsNotFound=fieldsNotFound;
    }


    public FieldsNotFoundResponse getErrorObject() {
        return new FieldsNotFoundResponse(this.message,this.fieldsNotFound);
    }
}


