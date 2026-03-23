package com.ckstjr.erroranalysis.reporter;

import com.ckstjr.erroranalysis.analyzer.ErrorAnalyzer;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResult;
import com.ckstjr.erroranalysis.slack.SlackNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

import static java.lang.Boolean.TRUE;

/**
 * 전역 예외 처리기에서 호출되어 실제 에러 분석 및 알림 과정을 오케스트레이션하는 역할 및 책임을 가집니다.
 * Redis를 활용한 Rate Limiting(알림 중복 방지)을 수행하며,
 * 메인 비즈니스 로직(사용자 응답)을 지연시키지 않기 위해 전체 흐름을 비동기(@Async)로 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmErrorReporter {

    private final ErrorAnalyzer llmErrorAnalyzer;
    private final SlackNotifier slackNotifier;
    private final StringRedisTemplate redisTemplate;

    // 동일한 에러에 대해 알림을 제한할 시간 (예: 10분)
    private static final Duration NOTIFY_DURATION = Duration.ofMinutes(10);

    /**
     * 예외 정보를 받아 분석 및 알림을 수행
     * 프론트엔드/클라이언트의 API 응답 속도에 영향을 주지 않도록 반드시 비동기로 동작
     * @param request 분석에 필요한 HTTP 요청 및 예외 정보
     */
    @Async
    public void report(ErrorAnalysisRequest request) {
        String cacheKey = createCacheKey(request);

        Boolean isLockSuccess = redisTemplate.opsForValue().setIfAbsent(cacheKey, TRUE.toString(), NOTIFY_DURATION);
        if (!isLockSuccess) {
            log.info("해당 에러는 최근 {}분 내에 이미 접수되었습니다. [key: {}]", NOTIFY_DURATION.toMinutes(), cacheKey);
            return;
        }

        ErrorAnalysisResponse response = llmErrorAnalyzer.analyze(request);
        log.info("에러 분석 완료. \"inference\":{}", response.getJson().getInference());

        notify(request, response.getJson(), cacheKey);
    }

    private void notify(ErrorAnalysisRequest request, ErrorAnalysisResult result, String cacheKey) {
        StringBuilder sb = new StringBuilder();

        sb.append(result.getAction()).append("\n");
        sb.append("```\n");
        sb.append("action: ").append(result.getAction()).append("\n");
        sb.append("request: ").append(request.getHttpMethod()).append(" ")
                .append(request.getPath()).append("\n");
        sb.append("reason: ").append(result.getReason()).append("\n");
        sb.append("solve: ").append(result.getGuide()).append("\n");
        sb.append("```");

        try {
            slackNotifier.send(sb.toString());
            // 알림 발송이 성공적으로 끝난 시점을 기준으로 다시 10분의 쿨타임을 갱신(덮어쓰기)합니다.
            redisTemplate.opsForValue().set(cacheKey, TRUE.toString(), NOTIFY_DURATION);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 발생한 예외의 동일성을 판단하기 위한 고유 키 생성
     */
    private String createCacheKey(ErrorAnalysisRequest request) {
        String httpMethod = request.getHttpMethod().toUpperCase();
        String path = request.getPath();
        Exception ex = request.getException();

        return String.format("%s:%s:%s:%s", request.getMemberId(), httpMethod, path, ex.getMessage());
    }

}
