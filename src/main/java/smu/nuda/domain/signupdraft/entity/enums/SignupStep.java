package smu.nuda.domain.signupdraft.entity.enums;

public enum SignupStep {
    ACCOUNT,
    DELIVERY,
    SURVEY,
    COMPLETED;

    public boolean isAfterOrEqual(SignupStep required) {
        return this.ordinal() >= required.ordinal();
    }
}
