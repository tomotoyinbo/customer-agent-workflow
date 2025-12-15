package io.bly.customer.agent.workflow.dto;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response shape returned by the API.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details) {

    public static ApiError of(int status,
                              String error,
                              String message,
                              String path,
                              List<String> details) {

        return new ApiError(Instant.now(), status, error, message, path, details);
    }
}
