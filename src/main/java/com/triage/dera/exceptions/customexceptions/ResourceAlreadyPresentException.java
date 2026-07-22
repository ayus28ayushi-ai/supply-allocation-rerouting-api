package com.triage.dera.exceptions.customexceptions;

public class ResourceAlreadyPresentException extends RuntimeException{
    public ResourceAlreadyPresentException(String message){
        super(message);
    }
}
