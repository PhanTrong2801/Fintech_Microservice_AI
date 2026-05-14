package com.repurpose.content_service.httpclient;

import com.repurpose.content_service.dto.RepurposeRequest;
import com.repurpose.content_service.dto.RepurposeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client để giao tiếp nội bộ với AI Pipeline Service
 * Tương tự pattern ProfileClient/WalletClient trong Fintech cũ
 */
@FeignClient(name = "ai-pipeline-service")
public interface AiPipelineClient {

    @PostMapping("/pipeline/repurpose")
    ResponseEntity<RepurposeResponse> repurpose(@RequestBody RepurposeRequest request);

    @PostMapping("/pipeline/generate-image")
    java.util.Map<String, String> generateImage(@RequestBody java.util.Map<String, String> request);
}
