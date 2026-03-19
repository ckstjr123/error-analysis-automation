package com.ckstjr.erroranalysis.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "flowise")
public class FlowiseProperties {
    /**
     * Flowise API Base URL
     */
    private String baseUrl;

    /**
     * Flowise Chatflow ID
     */
    private String chatflowId;

    /**
     * Optional Bearer Token for authorization
     */
    private String apiKey;
}
