package com.repurpose.content_service.dto;

import com.repurpose.content_service.entity.OutputFormat;
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
