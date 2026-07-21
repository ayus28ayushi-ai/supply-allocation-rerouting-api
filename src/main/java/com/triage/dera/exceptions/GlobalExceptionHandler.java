package com.triage.dera.exceptions;

import com.triage.dera.dto.ErrorResponseDto;
import jakarta.persistence.OptimisticLockException;
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
                .message("Item claimed by another unit. Try Again!")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
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
