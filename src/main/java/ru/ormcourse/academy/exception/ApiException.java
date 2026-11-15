package ru.ormcourse.academy.exception;

import java.io.Serial;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    private ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static <T> T require(Optional<T> result, String resource, long id) {
        return result.orElseThrow(() -> notFound(resource, id));
    }

    public static ApiException notFound(String resource, long id) {
        return new ApiException(HttpStatus.NOT_FOUND, resource + " #" + id + " was not found");
    }

    public static ApiException invalid(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }
}
