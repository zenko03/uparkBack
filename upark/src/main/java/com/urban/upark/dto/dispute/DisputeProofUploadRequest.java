package com.urban.upark.dto.dispute;

import lombok.Data;

@Data
public class DisputeProofUploadRequest {
    private String imageBase64;
    private String fileName;
    private Long userId;
    private Long disputeId;
}
