package com.abn.amro.recipe.resource.exception;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ResourceExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceExceptionHandler.class);
    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleGeneralException(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Unexpected Error occurred";
        LOG.error(ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(Response.builder().message(bodyOfResponse).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleBadRequestException(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        LOG.error(ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(Response.builder().message(bodyOfResponse).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    protected ResponseEntity<Object> handleResourceNotFoundException(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        LOG.error(ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(Response.builder().message(bodyOfResponse).build(), HttpStatus.NOT_FOUND);
    }

    @Data
    @Builder
    public static class Response{
        String message;
    }
}
