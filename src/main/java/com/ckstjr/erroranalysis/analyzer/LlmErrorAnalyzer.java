package com.ckstjr.erroranalysis.analyzer;

import com.ckstjr.erroranalysis.analyzer.methodsignature.MethodSignature;
import com.ckstjr.erroranalysis.analyzer.methodsignature.MethodSignatureParser;
import com.ckstjr.erroranalysis.client.FlowiseClient;
import com.ckstjr.erroranalysis.config.FlowiseProperties;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 실제 Flowise LLM API와 통신하여 에러를 분석하는 역할 및 책임을 가집니다.
 * 스택 트레이스 중 불필요한 로그를 필터링하여 토큰 비용을 절약하고,
 * 정제된 데이터를 바탕으로 Flowise Prediction API를 호출합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmErrorAnalyzer implements ErrorAnalyzer {

    private final FlowiseProperties flowiseProperties;
    private final MethodSignatureParser methodSignatureParser;
    private final FlowiseClient flowiseClient;

    @Override
    public ErrorAnalysisResponse analyze(ErrorAnalysisRequest request) {
        // 스택 트레이스 필터링
        List<StackTraceElement> filteredStackTrace = filterStackTrace(request.getException());

        // 상세 메시지와 필터링된 스택 트레이스를 하나의 문자열(cause)로 결합
        String cause = getStackTraceAsString(resolveDetailMessage(request.getException()), filteredStackTrace);

        // 바이트코드를 분석하여 추출한 메서드 시그니처 정보를 개행으로 연결하여 단일 문자열로 변환
        String methodSignatures = methodSignatureParser.parse(filteredStackTrace).stream()
                .map(MethodSignature::toString)
                .collect(Collectors.joining("\n"));

        // Feign Client를 통해 Flowise 호출
        return flowiseClient.analyzeError(
                flowiseProperties.getChatflowId(),
                Map.of(
                        "question", """
                                httpMethod: %s
                                requestUrl: %s
                                methodSignatures: %s
                                cause: %s
                                """.formatted(
                                request.getHttpMethod(),
                                request.getPath(),
                                methodSignatures,
                                cause
                        )
                )
        );
    }


    /**
     * 방대한 예외 스택 트레이스 중 프레임워크나 라이브러리 내부 로그를 제거하고,
     * 우리 프로젝트 내에서 발생한 스택 트레이스만 필터링합니다.
     *
     * @param exception 발생한 원본 예외 객체
     * @return 필터링된 스택 트레이스 요소 리스트
     */
    @Override
    public List<StackTraceElement> filterStackTrace(Exception exception) {
        // 스택 트레이스 중 프로젝트 패키지명을 포함하는 라인만 필터링
        return Arrays.stream(exception.getStackTrace())
                .filter(stackTrace -> stackTrace.getClassName().contains("ckstjr"))
                .collect(Collectors.toList());
    }

    private String resolveDetailMessage(Exception ex) {
        String message = ex.getMessage();

        if (ex instanceof MethodArgumentTypeMismatchException theEx) {
            return message + " on property " + theEx.getPropertyName();
        }

        return message;
    }

    /**
     * 에러 메시지와 필터링된 스택 트레이스 리스트를 읽기 좋은 하나의 문자열로 결합합니다.
     */
    private String getStackTraceAsString(String message, List<StackTraceElement> stackTraceElements) {
        StringBuilder sb = new StringBuilder();
        sb.append(message).append("\n");
        for (StackTraceElement stackTrace : stackTraceElements) {
            sb.append(stackTrace.toString()).append("\n");
        }
        return sb.toString();
    }

}
