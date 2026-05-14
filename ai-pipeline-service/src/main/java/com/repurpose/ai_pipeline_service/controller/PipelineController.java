package com.repurpose.ai_pipeline_service.controller;

import com.repurpose.ai_pipeline_service.dto.RepurposeRequest;
import com.repurpose.ai_pipeline_service.dto.RepurposeResponse;
import com.repurpose.ai_pipeline_service.service.PipelineService;
import com.repurpose.ai_pipeline_service.service.PromptTemplateService;
import com.repurpose.ai_pipeline_service.template.OutputFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pipeline")
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;
    private final PromptTemplateService promptTemplateService;

    /**
     * Endpoint chính - nhận nội dung gốc và danh sách format, trả về kết quả đã generate
     * Được gọi từ content-service qua OpenFeign
     */
    @PostMapping("/repurpose")
    public ResponseEntity<RepurposeResponse> repurpose(@RequestBody RepurposeRequest request) {
        RepurposeResponse response = pipelineService.repurpose(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách tất cả output format được hỗ trợ
     */
    @GetMapping("/formats")
    public ResponseEntity<OutputFormat[]> getFormats() {
        return ResponseEntity.ok(OutputFormat.values());
    }

    /**
     * Xem prompt template cho 1 format cụ thể
     */
    @GetMapping("/templates/{format}")
    public ResponseEntity<Map<String, String>> getTemplate(@PathVariable OutputFormat format) {
        return ResponseEntity.ok(Map.of(
                "format", format.name(),
                "template", promptTemplateService.getTemplate(format)
        ));
    }

    /**
     * Sinh ảnh minh họa bằng DALL-E 3
     */
    @PostMapping("/generate-image")
    public ResponseEntity<Map<String, String>> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String imageUrl = pipelineService.getAiProviderService().generateImage(prompt);
        
        if (imageUrl == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to generate image from AI provider"));
        }
        
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ai-pipeline-service",
                "provider", pipelineService.getAiProviderService().getProvider()
        ));
    }
}
