package com.jonas.eurder.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnexpected_givenUnknownException_thenReturns500WithoutInternalDetails() {
        ResponseEntity<String> response = handler.handleUnexpected(
                new RuntimeException("internal detail that must not leak")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Something went wrong.");
    }

    @Test
    void handleUnexpected_givenSpringErrorResponse_thenKeepsItsStatus() {
        ResponseEntity<String> response = handler.handleUnexpected(
                new HttpRequestMethodNotSupportedException("DELETE")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void handleIllegalArguments_thenReturns400WithTheMessage() {
        ResponseEntity<String> response = handler.handleIllegalArguments(
                new IllegalArgumentException("Price must be 0 or more")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Price must be 0 or more");
    }
}