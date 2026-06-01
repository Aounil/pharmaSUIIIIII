package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.exception.PharmacistExceptionHandler;
import com.pharmacy.shared.storage.StorageFileNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PharmacistExceptionHandlerTest {

    private final PharmacistExceptionHandler handler = new PharmacistExceptionHandler();

    @Test
    void handleIllegalState_shouldReturnConflictBody() {
        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/api/v1/pharmacist/prescriptions/1/ready");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalState(new IllegalStateException("Invalid state"), request);

        assertError(response, HttpStatus.CONFLICT, "Invalid state", request.getRequestURI());
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequestBody() {
        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/api/v1/pharmacist/prescriptions/1/reject");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Comment required"), request);

        assertError(response, HttpStatus.BAD_REQUEST, "Comment required", request.getRequestURI());
    }

    @Test
    void handleStorageFileNotFound_shouldReturnNotFoundBody() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/pharmacist/prescriptions/1/file");

        ResponseEntity<Map<String, Object>> response =
                handler.handleStorageFileNotFound(new StorageFileNotFoundException("missing"), request);

        assertError(response, HttpStatus.NOT_FOUND, "Prescription file not found", request.getRequestURI());
    }

    private void assertError(ResponseEntity<Map<String, Object>> response,
                             HttpStatus status,
                             String message,
                             String path) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).containsEntry("status", status.value())
                .containsEntry("error", status.getReasonPhrase())
                .containsEntry("message", message)
                .containsEntry("path", path);
        assertThat(response.getBody()).containsKey("timestamp");
    }
}
