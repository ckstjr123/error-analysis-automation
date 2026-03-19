package com.ckstjr.erroranalysis.dto;

import lombok.Getter;

@Getter
public class ErrorAnalysisResponse {
    /**
     * Flowise의 Structured Output Parser를 거쳐 매핑되는 JSON 결과
     */
    private ErrorAnalysisResult json;

    private String question;
    private String chatId;
    private String chatMessageId;
    private String sessionId;
}
