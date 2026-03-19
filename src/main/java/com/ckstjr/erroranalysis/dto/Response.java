package com.ckstjr.erroranalysis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Response<T> {

    private final String code;
    private final String message;
    private final T data;

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";

    public static <T> Response<T> of(String code, String message, T data) {
        return new Response<>(code, message, data);
    }
    
    public static <T> Response<T> success(T data) {
        return new Response<>(SUCCESS, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(SUCCESS, message, data);
    }
    
    public static Response<Void> success() {
        return new Response<>(SUCCESS, "요청이 성공적으로 처리되었습니다.", null);
    }

    public static <T> Response<T> error(String message, T data) {
        return new Response<>(ERROR, message, data);
    }

    public static Response<Void> error(String message) {
        return new Response<>(ERROR, message, null);
    }
}
