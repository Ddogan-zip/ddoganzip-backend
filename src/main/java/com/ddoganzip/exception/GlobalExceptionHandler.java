package com.ddoganzip.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("⚠️ [GlobalExceptionHandler] CustomException caught: {}", ex.getMessage(), ex);

        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
                .code("CUSTOM_ERROR")
                .message(ex.getMessage())
                .build();
        ErrorResponse response = ErrorResponse.builder()
                .error(errorDetail)
                .build();

        log.debug("[GlobalExceptionHandler] Returning 400 Bad Request with CustomException");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("⚠️ [GlobalExceptionHandler] Validation error occurred");

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("[GlobalExceptionHandler] Validation errors: {}", errors);

        String details = String.join(", ", errors);
        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
                .code("VALIDATION_ERROR")
                .message("입력 데이터가 유효하지 않습니다")
                .details(details)
                .build();
        ErrorResponse response = ErrorResponse.builder()
                .error(errorDetail)
                .build();

        log.debug("[GlobalExceptionHandler] Returning 400 Bad Request with validation errors");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("⚠️ [GlobalExceptionHandler] Type mismatch - Parameter: {}, Value: {}, Required type: {}",
            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        String message = String.format("파라미터 '%s'의 값이 올바르지 않습니다", ex.getName());
        String details = String.format("입력값 '%s'을(를) %s 타입으로 변환할 수 없습니다",
            ex.getValue(), ex.getRequiredType().getSimpleName());

        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
                .code("INVALID_PARAMETER")
                .message(message)
                .details(details)
                .build();
        ErrorResponse response = ErrorResponse.builder()
                .error(errorDetail)
                .build();

        log.info("[GlobalExceptionHandler] Returning 400 Bad Request for type mismatch");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("🔒 [GlobalExceptionHandler] BadCredentialsException - Login failed: {}", ex.getMessage());
        log.debug("[GlobalExceptionHandler] Full exception details:", ex);

        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
                .code("INVALID_CREDENTIALS")
                .message("이메일 또는 비밀번호가 일치하지 않습니다")
                .build();
        ErrorResponse response = ErrorResponse.builder()
                .error(errorDetail)
                .build();

        log.info("[GlobalExceptionHandler] Returning 401 Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("❌ [GlobalExceptionHandler] Unexpected exception occurred: {}", ex.getMessage(), ex);
        log.error("[GlobalExceptionHandler] Exception type: {}", ex.getClass().getName());

        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 오류가 발생했습니다")
                .details(ex.getMessage())
                .build();
        ErrorResponse response = ErrorResponse.builder()
                .error(errorDetail)
                .build();

        log.info("[GlobalExceptionHandler] Returning 500 Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
