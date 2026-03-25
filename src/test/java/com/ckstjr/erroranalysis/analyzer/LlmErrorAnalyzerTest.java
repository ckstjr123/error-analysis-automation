package com.ckstjr.erroranalysis.analyzer;

import com.ckstjr.erroranalysis.analyzer.methodsignature.MethodSignatureParser;
import com.ckstjr.erroranalysis.client.FlowiseClient;
import com.ckstjr.erroranalysis.config.FlowiseProperties;
import com.ckstjr.erroranalysis.api.ErrorController;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResult;
import com.ckstjr.erroranalysis.service.ErrorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmErrorAnalyzerTest {

    private static final String TEST_CHATFLOW_ID = "test-chatflow-id";
    private static final String TEST_EXCEPTION_MESSAGE = "테스트 예외 발생";

    @Spy
    private MethodSignatureParser methodSignatureParser = new MethodSignatureParser();

    @Spy
    private FlowiseProperties flowiseProperties = new FlowiseProperties("", TEST_CHATFLOW_ID, "");

    @Mock
    private FlowiseClient flowiseClient;

    @InjectMocks
    private LlmErrorAnalyzer llmErrorAnalyzer;

    @Test
    @DisplayName("지정된 스택 트레이스만 필터링되어야 함")
    void filterStackTrace() {
        // given
        Exception exception = new RuntimeException(TEST_EXCEPTION_MESSAGE);
        exception.setStackTrace(new StackTraceElement[]{
                new StackTraceElement(Thread.class.getName(), "run", Thread.class.getSimpleName() + ".java", 1),
                new StackTraceElement(ErrorController.class.getName(), "controller", ErrorController.class.getSimpleName() + ".java", 20),
                new StackTraceElement(ErrorService.class.getName(), "triggerException", ErrorService.class.getSimpleName() + ".java", 10),
                new StackTraceElement("org.springframework.web.servlet.DispatcherServlet", "doDispatch", "DispatcherServlet.java", 100)
        });

        // when
        List<StackTraceElement> filteredStackTrace = llmErrorAnalyzer.filterStackTrace(exception);

        // then
        assertThat(filteredStackTrace)
                .extracting(StackTraceElement::getClassName)
                .containsExactly(
                        ErrorController.class.getName(),
                        ErrorService.class.getName()
                );
    }

    @Test
    @DisplayName("발생한 에러 관련 정보를 전달하면 분석 결과가 응답되어야 함")
    void analyze() {
        final String httpMethod = "GET";
        final String path = "/api/test";
        final String question = httpMethod + " " + path + " " + TEST_EXCEPTION_MESSAGE + " " + ErrorService.class.getName();
        final String action = "게시글 조회";
        final String reason = "데이터베이스 연결 실패";
        final String guide = "DB 상태 확인";
        final String inference = "로그 분석 결과...";

        Exception exception = new RuntimeException(TEST_EXCEPTION_MESSAGE);
        exception.setStackTrace(new StackTraceElement[]{
                new StackTraceElement(ErrorService.class.getName(), "serviceMethod", ErrorService.class.getSimpleName() + ".java", 11)
        });

        ErrorAnalysisResponse expected = createErrorAnalysisResponse(question, action, reason, guide, inference);
        when(flowiseClient.predict(eq(TEST_CHATFLOW_ID), any())).thenReturn(expected);
        ErrorAnalysisRequest request = createErrorAnalysisRequest(httpMethod, path, exception);

        ErrorAnalysisResponse response = llmErrorAnalyzer.analyze(request);

        assertThat(response.getQuestion()).isEqualTo(question);
        assertThat(response.getJson().getAction()).isEqualTo(action);
        assertThat(response.getJson().getReason()).isEqualTo(reason);
        assertThat(response.getJson().getGuide()).isEqualTo(guide);
        assertThat(response.getJson().getInference()).isEqualTo(inference);
    }

    @Test
    @DisplayName("필터링된 스택 트레이스가 0개인 외부 라이브러리 예외여도 정상적으로 API를 호출해야 함")
    void analyze_WithNoFilteredStackTrace() {
        // given
        Exception exception = new RuntimeException("외부 라이브러리 예외 발생");
        exception.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("org.hibernate.exception.ConstraintViolationException", "execute", "ConstraintViolationException.java", 1),
                new StackTraceElement("org.springframework.web.servlet.DispatcherServlet", "doDispatch", "DispatcherServlet.java", 100)
        });

        ErrorAnalysisResponse expected = createErrorAnalysisResponse("question", "action", "reason", "guide", "inference");
        when(flowiseClient.predict(eq(TEST_CHATFLOW_ID), any())).thenReturn(expected);
        ErrorAnalysisRequest request = createErrorAnalysisRequest("POST", "/api/external", exception);

        // when
        ErrorAnalysisResponse response = llmErrorAnalyzer.analyze(request);

        // then
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("에러 분석 요청 객체의 Exception이 null이면 NullPointerException이 발생해야 함")
    void analyze_WhenExceptionIsNull() {
        ErrorAnalysisRequest request = createErrorAnalysisRequest("GET", "/api/test", null);

        assertThatThrownBy(() -> llmErrorAnalyzer.analyze(request))
                .isInstanceOf(NullPointerException.class);
    }


    private ErrorAnalysisRequest createErrorAnalysisRequest(String httpMethod, String path, Exception exception) {
        return ErrorAnalysisRequest.builder()
                .httpMethod(httpMethod)
                .path(path)
                .exception(exception)
                .build();
    }

    private ErrorAnalysisResponse createErrorAnalysisResponse(String question, String action, String reason, String guide, String inference) {
        ErrorAnalysisResult result = new ErrorAnalysisResult(action, reason, guide, inference);
        return new ErrorAnalysisResponse(result, question, "chatId", "chatMessageId", "sessionId");
    }

}
