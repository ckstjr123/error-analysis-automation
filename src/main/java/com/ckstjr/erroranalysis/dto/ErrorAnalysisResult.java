package com.ckstjr.erroranalysis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ErrorAnalysisResult {
    /**
     * 에러 발생 시 유저가 시도하려던 행동
     */
    private String action;

    /**
     * 서버 에러가 발생한 근본 원인
     */
    private String reason;

    /**
     * 프론트엔드 개발자나 PM이 취해야 할 조치 사항
     */
    private String guide;

    /**
     * LLM의 내부 추론 과정 (Chain of Thought)
     */
    private String inference;
}
