package com.ckstjr.erroranalysis.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "flowise")
public class FlowiseProperties {
    /**
     * Flowise API Base URL
     */
    private final String baseUrl;

    /**
     * Flowise Chatflow ID
     */
    private final String chatflowId;

    /**
     * Optional Bearer Token for authorization
     */
    private final String apiKey;
}
