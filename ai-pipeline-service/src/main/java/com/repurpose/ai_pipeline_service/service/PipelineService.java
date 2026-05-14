package com.repurpose.ai_pipeline_service.service;

import com.repurpose.ai_pipeline_service.dto.*;
import com.repurpose.ai_pipeline_service.template.OutputFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator chính của AI Pipeline.
 * Nhận request → xây prompt → gọi AI → trả kết quả.
 */
@Service
@RequiredArgsConstructor
@Getter
public class PipelineService {

    private final PromptTemplateService promptTemplateService;
    private final AiProviderService aiProviderService;

    /**
     * Xử lý repurpose request - tạo nội dung cho tất cả format được yêu cầu
     */
    public RepurposeResponse repurpose(RepurposeRequest request) {
        List<RepurposeResult> results = new ArrayList<>();

        try {
            for (OutputFormat format : request.getOutputFormats()) {
                // 1. Xây prompt từ template
                String prompt = promptTemplateService.getPrompt(format, request.getOriginalContent());

                // 2. Gọi AI Provider để generate
                String generatedContent = aiProviderService.generate(prompt, format);

                // 3. Ước tính tokens
                int tokensUsed = aiProviderService.estimateTokens(generatedContent);

                // 4. Tạo result
                RepurposeResult result = RepurposeResult.builder()
                        .outputFormat(format)
                        .generatedContent(generatedContent)
                        .tokensUsed(tokensUsed)
                        .build();
                results.add(result);

                System.out.println("✅ Generated " + format.name() + " (" + tokensUsed + " tokens)");
            }

            return RepurposeResponse.builder()
                    .success(true)
                    .message("Đã tái chế nội dung thành công cho " + results.size() + " định dạng!")
                    .results(results)
                    .build();

        } catch (Exception e) {
            System.err.println("❌ Lỗi trong pipeline: " + e.getMessage());
            return RepurposeResponse.builder()
                    .success(false)
                    .message("Lỗi xử lý: " + e.getMessage())
                    .results(results) // Trả về những kết quả đã generate được
                    .build();
        }
    }
}
