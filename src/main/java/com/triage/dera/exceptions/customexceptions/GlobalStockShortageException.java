package com.triage.dera.exceptions.customexceptions;

public class GlobalStockShortageException extends RuntimeException {
    public GlobalStockShortageException(String message){
        super(message);
    }
}
