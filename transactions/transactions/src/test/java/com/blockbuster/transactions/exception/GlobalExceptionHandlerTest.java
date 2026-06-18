package com.blockbuster.transactions.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleTransactionException() {
        ResponseEntity<ApiErrorResponse> response = handler.handleTransactionException(
                new TransactionException(HttpStatus.CONFLICT, "Stock insuficiente"),
                request("/api/v1/rentals"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Stock insuficiente", response.getBody().getMessage());
        assertEquals("/api/v1/rentals", response.getBody().getPath());
    }

    @Test
    void shouldHandleValidationExceptionUsingFirstFieldMessage() throws Exception {
        SampleBody body = new SampleBody();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(body, "body");
        bindingResult.rejectValue("field", "required", "El campo es obligatorio");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationExceptions(
                validationException(bindingResult),
                request("/api/v1/rentals"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El campo es obligatorio", response.getBody().getMessage());
    }

    @Test
    void shouldHandleValidationExceptionUsingFallbackMessage() throws Exception {
        SampleBody body = new SampleBody();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(body, "body");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationExceptions(
                validationException(bindingResult),
                request("/api/v1/rentals"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Solicitud invalida", response.getBody().getMessage());
    }

    @Test
    void shouldHandleAccessDenied() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("denied"),
                request("/api/v1/rentals/user/6"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso denegado", response.getBody().getMessage());
    }

    @Test
    void shouldHandleRuntimeException() {
        ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeExceptions(
                new RuntimeException("boom"),
                request("/api/v1/rentals"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrio un error interno en el microservicio de transacciones", response.getBody().getMessage());
    }

    private MethodArgumentNotValidException validationException(BeanPropertyBindingResult bindingResult) throws Exception {
        Method method = SampleController.class.getDeclaredMethod("create", SampleBody.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        return new MethodArgumentNotValidException(parameter, bindingResult);
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    static class SampleController {
        @SuppressWarnings("unused")
        void create(SampleBody body) {
        }
    }

    static class SampleBody {
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
