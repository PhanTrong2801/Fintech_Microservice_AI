package com.repurpose.ai_pipeline_service.dto;

import com.repurpose.ai_pipeline_service.template.OutputFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepurposeResult {
    private OutputFormat outputFormat;
    private String generatedContent;
    private Integer tokensUsed;
}
