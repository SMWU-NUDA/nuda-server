package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreferToggleResponse {
    private Boolean preference;

    public static PreferToggleResponse interested() {
        return new PreferToggleResponse(true);
    }

    public static PreferToggleResponse avoided() {
        return new PreferToggleResponse(false);
    }

    public static PreferToggleResponse none() {
        return new PreferToggleResponse(null);
    }
}
