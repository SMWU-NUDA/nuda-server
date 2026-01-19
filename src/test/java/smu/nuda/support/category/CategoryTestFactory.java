package smu.nuda.support.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.enums.CategoryCode;
import smu.nuda.domain.product.repository.CategoryRepository;

@Component
public class CategoryTestFactory {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getOrCreate(CategoryCode code) {
        return categoryRepository.findByCode(code)
                .orElseGet(() ->
                        categoryRepository.save(
                                Category.builder()
                                        .code(code)
                                        .name("테스트 카테고리")
                                        .build()
                        )
                );
    }
}

