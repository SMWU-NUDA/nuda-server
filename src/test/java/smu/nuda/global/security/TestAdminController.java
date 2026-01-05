package smu.nuda.global.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class TestAdminController {

    @GetMapping("/secret")
    public String secret() {
        return "secret";
    }
}

