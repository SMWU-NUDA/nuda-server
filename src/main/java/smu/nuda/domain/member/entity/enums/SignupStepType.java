package smu.nuda.domain.member.entity.enums;

public enum SignupStepType {
    SIGNUP, // 기본 회원정보 입력 완료
    DELIVERY, // 배송지 입력 완료
    SURVEY, // 설문 완료
    COMPLETED; // 회원가입 최종 완료

    public boolean isAfterOrEqual(SignupStepType required) {
        return this.ordinal() >= required.ordinal();
    }
}