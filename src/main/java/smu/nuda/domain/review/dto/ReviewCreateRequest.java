package smu.nuda.domain.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class ReviewCreateRequest {
    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;

    private String pros;
    private String cons;

    private List<String> imageUrls = new ArrayList<>(); // S3 presigned URL
}
