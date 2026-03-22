package com.ckstjr.erroranalysis.exception;

import com.ckstjr.erroranalysis.aop.ErrorReport;
import com.ckstjr.erroranalysis.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ErrorReport
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception ex) {
        log.warn("Exception occurred", ex);
        return Response.error("서버 내부에서 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException caught: {}", ex.getMessage());
        return Response.error("잘못된 요청 파라미터입니다. (상세: " + ex.getMessage() + ")");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public Response<Void> handleIllegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException caught: {}", ex.getMessage());
        return Response.error("현재 요청을 처리할 수 없는 상태입니다. (상세: " + ex.getMessage() + ")");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public Response<Void> handleNullPointerException(NullPointerException ex) {
        log.warn("NullPointerException occurred", ex);
        return Response.error("요청 처리 중 필요한 값이 없어 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ErrorReport.Exclude
    @ExceptionHandler(ArithmeticException.class)
    public Response<Void> handleArithmeticException() {
        // log.warn("ArithmeticException occurred", ex);
        return Response.error("서버에서 연산 처리 중 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Response<Void> handleIndexOutOfBoundsException(IndexOutOfBoundsException ex) {
        log.warn("IndexOutOfBoundsException occurred", ex);
        return Response.error("요청하신 데이터의 접근 범위를 벗어났습니다.");
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ErrorReport.Exclude
    @ExceptionHandler(UnsupportedOperationException.class)
    public Response<Void> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        log.warn("UnsupportedOperationException occurred", ex);
        return Response.error("현재 서버에서 지원하지 않는 기능에 대한 요청입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException {}", ex.getName(), ex);
        return Response.error("요청 파라미터 형식이 올바르지 않습니다.", "파라미터명: " + ex.getName());
    }

}
