package com.ckstjr.erroranalysis.reporter;

import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class ErrorReporter {

    private final LlmErrorReporter llmErrorReporter;

    public void report(Exception ex, HttpServletRequest request) {
        // LLM 분석 요청 객체 생성 (요청 정보 + 원본 예외)
        ErrorAnalysisRequest errorAnalysisRequest = ErrorAnalysisRequest.builder()
//                .memberId(memberId)
                .httpMethod(request.getMethod())
                .path(getBestMatchingPattern(request))
                .exception(ex)
                .build();

        llmErrorReporter.report(errorAnalysisRequest);
    }

    /**
     * Spring의 HandlerMapping에서 Path Variable이 치환되기 전의 API 패턴을 추출합니다.
     * 예: /api/users/1 -> /api/users/{id}
     */
    private String getBestMatchingPattern(HttpServletRequest request) {
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return pattern != null ? pattern.toString() : request.getRequestURI();
    }

}
