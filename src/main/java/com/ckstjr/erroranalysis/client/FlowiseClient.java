package com.ckstjr.erroranalysis.client;

import com.ckstjr.erroranalysis.config.FeignClientConfig;
import com.ckstjr.erroranalysis.dto.ErrorAnalysisResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "flowiseClient", url = "${flowise.base-url}", configuration = FeignClientConfig.class)
public interface FlowiseClient {

    @PostMapping("/api/v1/prediction/{chatflowId}")
    ErrorAnalysisResponse analyzeError(
            @PathVariable("chatflowId") String chatflowId,
            @RequestBody Map<String, String> request
    );
}
