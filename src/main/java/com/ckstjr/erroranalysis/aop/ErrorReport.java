package com.ckstjr.erroranalysis.aop;

import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AOP를 통해 Api에서 발생하는 예외를 분석 및 보고하기 위한 마커(Marker) 어노테이션입니다.
 * <p>
 * 이 어노테이션은 {@link ErrorReportAspect}에 의해 처리되며, 반드시 ExceptionHandler와 함께
 * 클래스 레벨에 선언되어야 합니다.
 *
 * @see ErrorReportAspect
 * @see ExceptionHandler
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorReport {

    /**
     * 이 어노테이션이 붙은 메서드는 예외 리포팅 대상에서 제외됩니다.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Exclude {}
}

