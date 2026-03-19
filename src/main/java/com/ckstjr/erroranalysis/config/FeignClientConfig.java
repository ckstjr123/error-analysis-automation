package com.ckstjr.erroranalysis.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class FeignClientConfig {

    private final FlowiseProperties flowiseProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + flowiseProperties.getApiKey());
            }
        };
    }
}
