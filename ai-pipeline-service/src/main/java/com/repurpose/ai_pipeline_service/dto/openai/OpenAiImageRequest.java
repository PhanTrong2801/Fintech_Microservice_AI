package com.repurpose.ai_pipeline_service.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho OpenAI Image Generation API (DALL-E 3)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiImageRequest {
    @Builder.Default
    private String model = "dall-e-3";
    private String prompt;
    @Builder.Default
    private int n = 1;
}
