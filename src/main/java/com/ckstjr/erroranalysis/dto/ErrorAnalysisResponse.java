package com.ckstjr.erroranalysis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorAnalysisResponse {
    /**
     * Flowise의 Structured Output Parser를 거쳐 매핑되는 JSON 결과
     */
    private final ErrorAnalysisResult json;

    private final String question;
    private final String chatId;
    private final String chatMessageId;
    private final String sessionId;
}
