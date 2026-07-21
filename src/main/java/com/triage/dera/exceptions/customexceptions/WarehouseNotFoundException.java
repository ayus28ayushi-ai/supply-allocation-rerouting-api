package com.triage.dera.exceptions.customexceptions;

public class WarehouseNotFoundException extends RuntimeException{
    public WarehouseNotFoundException(String message){
        super(message);
    }
}
