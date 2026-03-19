package com.ckstjr.erroranalysis.analyzer;

import com.ckstjr.erroranalysis.dto.ErrorAnalysisRequest;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;

import java.util.List;

/**
 * 에러 데이터를 가공하고 LLM 파이프라인(Flowise 등)으로 전달하여 분석 결과를 반환하는 역할 및 책임을 가집니다.
 * 추후 다른 서비스(OpenAI API 직접 호출, LangChain4j 등)로 교체되더라도
 * 에러 리포팅 로직(ErrorReporter)의 변경을 최소화하기 위해 인터페이스로 분리합니다.
 */
public interface ErrorAnalyzer {

    /**
     * 에러 관련 정보가 담긴 요청 객체를 받아 분석 결과를 반환합니다.
     * @param request 분석에 필요한 컨텍스트(HTTP Method, Path, 에러 원본 객체 등)
     * @return LLM이 반환한 응답 객체 (JSON 형식 결과 포함)
     */
    ErrorAnalysisResponse analyze(ErrorAnalysisRequest request);

    /**
     * 방대한 예외 스택 트레이스 중 프레임워크나 라이브러리 내부 로그를 제거하고,
     * 우리 프로젝트 내에서 발생한 스택 트레이스만 필터링합니다.
     * @param exception 발생한 원본 예외 객체
     * @return 필터링된 스택 트레이스 요소 리스트
     */
    List<StackTraceElement> filterStackTrace(Exception exception);
}
