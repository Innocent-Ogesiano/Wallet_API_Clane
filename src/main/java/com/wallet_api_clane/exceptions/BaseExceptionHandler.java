package com.wallet_api_clane.exceptions;

import com.wallet_api_clane.response.ApiResponse;
import com.wallet_api_clane.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.wallet_api_clane.utils.UserUtil.createHttpResponse;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
                    if (errors.containsKey(error.getField())) {
                        errors.put(error.getField(), String.format("%s, %s", errors.get(error.getField()), error.getDefaultMessage()));
                    } else {
                        errors.put(error.getField(), error.getDefaultMessage());
                    }
                }
        );
        return new ApiResponse(errors, "VALIDATION_FAILED");
    }

    @ExceptionHandler(AlreadyUpgradedException.class)
    public ResponseEntity<HttpResponse> handleAlreadyUpgradedException (AlreadyUpgradedException exception) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());

    }

    @ExceptionHandler(UserWithEmailNotFound.class)
    public ResponseEntity<HttpResponse> handleUserWithEmailNotFoundException (UserWithEmailNotFound exception) {
        return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<HttpResponse> handleResourceAlreadyExistException (ResourceAlreadyExistException exception) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<HttpResponse> handleInvalidAmountException(InvalidAmountException exception) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> handleIOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<HttpResponse> handleInvalidTransactionException(InvalidTransactionException s) {
        return createHttpResponse(HttpStatus.FORBIDDEN, s.getMessage());
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<HttpResponse> handleAccountNotVerifiedException(AccountNotVerifiedException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(TransactionLimitException.class)
    public ResponseEntity<HttpResponse> handleTransactionLimitException(TransactionLimitException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientResourcesException.class)
    public ResponseEntity<HttpResponse> handleInsufficientResourcesException(InsufficientResourcesException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(TransactionDeclinedException.class)
    public ResponseEntity<HttpResponse> handleTransactionDeclinedException(TransactionDeclinedException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotQualifiedException.class)
    public ResponseEntity<HttpResponse> handleNotQualifiedException(NotQualifiedException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}
