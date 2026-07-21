package com.triage.dera.exceptions;

import com.triage.dera.entity.Warehouse;

public class WarehouseNotFoundException extends RuntimeException{
    public WarehouseNotFoundException(String message){
        super(message);
    }
}
