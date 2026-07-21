package com.triage.dera.exceptions;

import com.triage.dera.dto.ErrorResponseDto;
import com.triage.dera.exceptions.customexceptions.GlobalStockShortageException;
import com.triage.dera.exceptions.customexceptions.InsufficientStockException;
import com.triage.dera.exceptions.customexceptions.ResourceNotFoundException;
import com.triage.dera.exceptions.customexceptions.WarehouseNotFoundException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Item not there in the requested warehouse
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message("Resource not there in this warehouse.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    //Invalid request related to the item
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientStock(InsufficientStockException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message("Invalid resource request.")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    //Global Stock Shortage
    @ExceptionHandler(GlobalStockShortageException.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalStockShortage(GlobalStockShortageException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("CONFLICT")
                .message("Global stock shortage. Try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    //warehouse not found
    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleWarehouseNotFound(WarehouseNotFoundException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message("Warehouse not found.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    //DTO validation failure
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMethodArgument(MethodArgumentNotValidException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_FAILURE")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    //Optimistic Lock Conflict
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDto> handleObjectOptimisticLockException(ObjectOptimisticLockingFailureException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("CONCURRENCY_CONFLICT")
                .message("Stock updated by another request. Please refresh and retry.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    //handles pessimistic locking failure exceptions (eg. deadlocks)
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDto> handlePessimisticLockingFailureException(PessimisticLockingFailureException ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("HIGH_TRAFFIC")
                .message("System is experiencing high load on this warehouse stock. Try again in a few seconds.")
                .build();
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
    }

    //General system error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex){
        ErrorResponseDto err = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
