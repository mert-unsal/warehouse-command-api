package com.ikea.warehouse_command_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException_notFoundMessageReturns404() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/some/path");
        IllegalArgumentException ex = new IllegalArgumentException("Article not found: 123");
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArgumentException(ex, req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("NOT_FOUND", resp.getBody().error());
    }

    @Test
    void handleIllegalArgumentException_otherMessageReturns400() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/some/path");
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArgumentException(ex, req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("INVALID_ARGUMENT", resp.getBody().error());
        assertEquals("Invalid parameter", resp.getBody().message());
    }
}
