package com.ckstjr.erroranalysis.reporter;

import com.ckstjr.erroranalysis.analyzer.ErrorAnalyzer;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResult;
import com.ckstjr.erroranalysis.slack.SlackNotifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmErrorReporterTest {

    @Mock
    private ErrorAnalyzer errorAnalyzer;

    @Mock
    private SlackNotifier slackNotifier;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LlmErrorReporter llmErrorReporter;

    @Test
    @DisplayName("Redis에 해당 에러에 대한 캐시가 없어 true를 반환하면 에러 분석 로직이 실행되어야 함")
    void shouldCallAnalyze_whenCacheKeyNotExists() {
        ErrorAnalysisRequest request = createErrorAnalysisRequest();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);
        ErrorAnalysisResult result = new ErrorAnalysisResult("action", "reason", "guide", "inference");
        ErrorAnalysisResponse response = new ErrorAnalysisResponse(result, "question", "chatId", "chatMessageId", "sessionId");
        when(errorAnalyzer.analyze(request)).thenReturn(response);

        llmErrorReporter.report(request);

        verify(errorAnalyzer).analyze(request);
    }

    @Test
    @DisplayName("Redis에 이미 해당 에러에 대한 키가 존재하면 에러 분석 로직이 실행되지 않아야 함")
    void shouldNotExecuteAnalyze_whenCacheKeyExists() {
        ErrorAnalysisRequest request = createErrorAnalysisRequest();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(false);

        llmErrorReporter.report(request);

        verify(errorAnalyzer, never()).analyze(request);
    }

    private ErrorAnalysisRequest createErrorAnalysisRequest() {
        return ErrorAnalysisRequest.builder()
                .memberId(1L)
                .httpMethod("GET")
                .path("/api/test")
                .exception(new RuntimeException("Test Exception"))
                .build();
    }
}
