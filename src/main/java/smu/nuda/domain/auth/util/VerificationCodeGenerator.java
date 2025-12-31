package smu.nuda.domain.auth.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationCodeGenerator {

    public String generate() {
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }
}
