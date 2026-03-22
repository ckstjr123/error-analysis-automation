package com.ckstjr.erroranalysis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ErrorAnalysisRequest {
    /**
     * 에러를 발생시킨 사용자 ID (userId)
     * 용도: 에러 리포팅 시 동일 사용자의 중복 알림 방지(Rate Limiting)를 위한 캐시 키 생성 등에 사용됩니다.
     */
    private Long memberId;

    /**
     * HTTP 메서드
     */
    private String httpMethod;

    /**
     * 에러가 발생한 요청 경로
     */
    private String path;

    /**
     * 발생한 예외 객체 원본
     */
    private Exception exception;

    /**
     * 알림 전송 여부 플래그
     * 용도: 에러 분석 후 Slack 등의 채널로 실제 알림을 전송할지 여부를 결정합니다. 특정 조건에서는 분석만 하고 알림은 스킵할 수 있습니다.
     */
    private Boolean shouldNotify;

    /**
     * 외부 로그 시스템의 로그 ID
     * 용도: Slack 알림 전송 시 Kibana, Datadog 등 외부 로그 시스템으로 바로 이동할 수 있는 딥링크(Deep Link)를 생성하기 위해 사용됩니다.
     */
    private String logId;
}
