package Back_End.controller;

import Back_End.dto.ErrorResponse;
import Back_End.exception.ForbiddenException;
import Back_End.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (var e : ex.getBindingResult().getAllErrors()) {
      String field = e instanceof FieldError fe ? fe.getField() : e.getObjectName();
      errors.put(field, e.getDefaultMessage());
    }
    var body = new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Validation error",
        "Invalid request", req.getRequestURI(), errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
    var body = new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Validation error",
        "Invalid request", req.getRequestURI(), errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
    var body = new ErrorResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found",
        ex.getMessage(), req.getRequestURI(), null);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
  public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex, HttpServletRequest req) {
    var body = new ErrorResponse(Instant.now(), HttpStatus.FORBIDDEN.value(), "Forbidden",
        ex.getMessage(), req.getRequestURI(), null);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
    var body = new ErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
        ex.getMessage(), req.getRequestURI(), null);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}

