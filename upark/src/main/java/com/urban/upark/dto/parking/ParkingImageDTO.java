package com.urban.upark.dto.parking;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingImageDTO {
    
    @JsonProperty("filePath")
    private String filePath;
    
    @JsonProperty("fileUrl")
    private String fileUrl;
    
    @JsonProperty("fileSize")
    private Integer fileSize;
    
    @JsonProperty("isPrimary")
    private Boolean isPrimary;
}
