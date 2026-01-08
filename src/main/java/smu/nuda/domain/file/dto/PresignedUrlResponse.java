package smu.nuda.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PresignedUrlResponse {
    private String uploadUrl;
    private String imageUrl;
}
