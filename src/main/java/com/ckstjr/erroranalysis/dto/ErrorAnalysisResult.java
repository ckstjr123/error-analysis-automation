package com.ckstjr.erroranalysis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ErrorAnalysisResult {
    /**
     * 에러 발생 시 유저가 시도하려던 행동
     */
    private final String action;

    /**
     * 서버 에러가 발생한 근본 원인
     */
    private final String reason;

    /**
     * 프론트엔드 개발자나 PM이 취해야 할 조치 사항
     */
    private final String guide;

    /**
     * LLM의 내부 추론 과정 (Chain of Thought)
     */
    private final String inference;
}
