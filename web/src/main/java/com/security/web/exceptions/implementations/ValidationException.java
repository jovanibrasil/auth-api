package com.security.web.exceptions.implementations;

import java.util.Arrays;
import java.util.List;

public class ValidationException extends RuntimeException {

    private List<String> errorMessages;

    public ValidationException(List<String> errorList) {
        super(errorList.toString());
        this.errorMessages = errorList;
    }

    public ValidationException(String errorMessage) {
        super(errorMessage);
        this.errorMessages = Arrays.asList(errorMessage);
    }

    public List<String> getErrorMessages(){
        return this.errorMessages;
    }

}