package ru.ormcourse.academy.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class Responses {
    private Responses() {
    }

    public static <T> ResponseEntity<T> created(long id, T body) {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(id).toUri()).body(body);
    }
}
