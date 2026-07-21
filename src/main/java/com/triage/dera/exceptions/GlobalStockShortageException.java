package com.triage.dera.exceptions;

public class GlobalStockShortageException extends RuntimeException {
    public GlobalStockShortageException(String message){
        super(message);
    }
}
