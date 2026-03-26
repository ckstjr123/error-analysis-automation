package com.ckstjr.erroranalysis.aop;

import com.ckstjr.erroranalysis.reporter.ErrorReporter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ErrorReportAspect {

    private final ErrorReporter errorReporter;

    @Pointcut("@within(com.ckstjr.erroranalysis.aop.ErrorReport) " +
              "&& @annotation(org.springframework.web.bind.annotation.ExceptionHandler) " +
              "&& !@annotation(com.ckstjr.erroranalysis.aop.ErrorReport.Exclude)")
    public void exceptionHandler() {}

    @After("exceptionHandler()")
    public void reportError(JoinPoint joinPoint) {
        HttpServletRequest request = findRequest();
        if (request == null) {
            return;
        }

        Exception ex = findArg(joinPoint.getArgs(), Exception.class); // Exception 파라미터는 컴파일 타임에 보장됨
        errorReporter.report(ex, request);
    }

    private HttpServletRequest findRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getRequest() : null;
    }

    private <T> T findArg(Object[] args, Class<T> clazz) {
        return Arrays.stream(args)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
