package com.ikea.warehouse_command_api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.core.MethodParameter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerMoreTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleJsonProcessingException_returnsBadRequest() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/test");
        JsonProcessingException ex = new JsonProcessingException("Bad token"){};
        ResponseEntity<ErrorResponse> resp = handler.handleJsonProcessingException(ex, req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().message().contains("Bad token"));
    }

    @Test
    void handleHttpMediaTypeNotSupportedException_returns415() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/test");
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");
        ResponseEntity<ErrorResponse> resp = handler.handleHttpMediaTypeNotSupportedException(ex, req);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, resp.getStatusCode());
    }

    @Test
    void handleMethodArgumentNotValid_inventoryPathReturnsInventoryMessage() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/commands/inventory");
        class Dummy { public void m(String p){} }
        java.lang.reflect.Method m = Dummy.class.getDeclaredMethod("m", String.class);
        MethodParameter mp = new MethodParameter(m, 0);
        BindException bindEx = new BindException(new Object(), "dummy");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, bindEx.getBindingResult());
        ResponseEntity<ErrorResponse> resp = handler.handleMethodArgumentNotValid(ex, req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("FILE_PROCESSING_ERROR", resp.getBody().error());
        assertTrue(resp.getBody().message().contains("inventory"));
    }

    @Test
    void handleOptimisticLocking_returns409() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/commands/articles/1");
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("Version conflict");
        ResponseEntity<ErrorResponse> resp = handler.handleOptimisticLocking(ex, req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("OPTIMISTIC_LOCK_CONFLICT", resp.getBody().error());
    }

    @Test
    void handleGenericException_returns500() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/any");
        Exception ex = new Exception("boom");
        ResponseEntity<ErrorResponse> resp = handler.handleGenericException(ex, req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", resp.getBody().error());
    }
}
