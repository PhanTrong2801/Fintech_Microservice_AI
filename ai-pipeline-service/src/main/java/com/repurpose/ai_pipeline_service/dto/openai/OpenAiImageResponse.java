package com.repurpose.ai_pipeline_service.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO cho OpenAI Image Generation API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiImageResponse {
    private long created;
    private List<ImageData> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        private String url;
        private String revised_prompt;
    }
}
