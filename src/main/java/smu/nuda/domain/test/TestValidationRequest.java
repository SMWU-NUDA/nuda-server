package smu.nuda.domain.test;

import jakarta.validation.constraints.NotBlank;

public class TestValidationRequest {

    @NotBlank(message = "message는 공백일 수 없습니다")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
