package com.repurpose.content_service.dto;

import com.repurpose.content_service.entity.OutputFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response trả về từ AI Pipeline Service sau khi repurpose
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepurposeResponse {
    private boolean success;
    private String message;
    private List<RepurposeResult> results;
}
