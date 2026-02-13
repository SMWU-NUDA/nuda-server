package smu.nuda.domain.keyword.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keywords")
@Tag(name = "[KEYWORD] 키워드 API", description = "사용자의 키워드 API")
public class KeywordController {
}
