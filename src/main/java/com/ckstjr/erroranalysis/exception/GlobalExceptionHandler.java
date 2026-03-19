package com.ckstjr.erroranalysis.exception;

import com.ckstjr.erroranalysis.aop.ErrorReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ErrorReport
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception ex) {
        log.warn("Exception occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 에러가 발생했습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException caught: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("잘못된 인자입니다: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException caught: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("잘못된 상태입니다: " + ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        log.warn("NullPointerException occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("널 포인터 참조");
    }

    @ExceptionHandler(ArithmeticException.class)
    @ErrorReport.Exclude
    public ResponseEntity<String> handleArithmeticException() {
//        log.warn("ArithmeticException occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("숫자 연산 중 예외가 발생했습니다.");
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException ex) {
        log.warn("IndexOutOfBoundsException occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("인덱스 범위 초과");
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ErrorReport.Exclude
    public ResponseEntity<String> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        log.warn("UnsupportedOperationException occurred", ex);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("지원하지 않는 기능입니다: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException {}", ex.getName(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청 파라미터 형식입니다: " + ex.getName());
    }

}