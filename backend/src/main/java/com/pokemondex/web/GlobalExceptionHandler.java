package com.pokemondex.web;

import com.pokemondex.domain.exception.AuthenticationFailedException;
import com.pokemondex.domain.exception.DuplicateEmailException;
import com.pokemondex.domain.exception.ExternalServiceException;
import com.pokemondex.domain.exception.InvalidRequestException;
import com.pokemondex.domain.exception.LocalPokemonNotFoundException;
import com.pokemondex.domain.exception.PokemonNotFoundException;
import com.pokemondex.domain.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleInvalidBody(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ApiError.FieldErrorItem> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldErrorItem(error.getField(), error.getDefaultMessage()))
                .toList();
        return badRequest(request, "Validation failed", fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> handleConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        List<ApiError.FieldErrorItem> fields = exception.getConstraintViolations().stream()
                .map(violation -> new ApiError.FieldErrorItem(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return badRequest(request, "Validation failed", fields);
    }

    @ExceptionHandler({InvalidRequestException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> handleBadRequest(Exception exception, HttpServletRequest request) {
        String message = exception instanceof MethodArgumentTypeMismatchException mismatch
                ? "Invalid value for " + mismatch.getName()
                : exception.getMessage();
        if (exception instanceof HttpMessageNotReadableException) {
            message = "Malformed request body";
        }
        return badRequest(request, message, List.of());
    }

    @ExceptionHandler({PokemonNotFoundException.class, LocalPokemonNotFoundException.class, UserNotFoundException.class})
    ResponseEntity<ApiError> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return status(HttpStatus.NOT_FOUND, request, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    ResponseEntity<ApiError> handleUnauthorized(AuthenticationFailedException exception, HttpServletRequest request) {
        return status(HttpStatus.UNAUTHORIZED, request, exception.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ApiError> handleConflict(DuplicateEmailException exception, HttpServletRequest request) {
        return status(HttpStatus.CONFLICT, request, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) {
        return status(HttpStatus.CONFLICT, request, "The record conflicts with an existing Pokemon or user");
    }

    @ExceptionHandler(ExternalServiceException.class)
    ResponseEntity<ApiError> handleBadGateway(ExternalServiceException exception, HttpServletRequest request) {
        return status(HttpStatus.BAD_GATEWAY, request, exception.getMessage());
    }

    private static ResponseEntity<ApiError> badRequest(HttpServletRequest request, String message, List<ApiError.FieldErrorItem> fields) {
        return ResponseEntity.badRequest().body(ApiError.of(400, "Bad Request", message, request.getRequestURI(), fields));
    }

    private static ResponseEntity<ApiError> status(HttpStatus status, HttpServletRequest request, String message) {
        return ResponseEntity.status(status).body(ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                List.of()
        ));
    }
}
