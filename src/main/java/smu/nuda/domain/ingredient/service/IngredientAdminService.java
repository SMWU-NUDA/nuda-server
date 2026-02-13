package smu.nuda.domain.ingredient.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.ingredient.csv.IngredientCsvReader;
import smu.nuda.domain.ingredient.dto.IngredientCsvRow;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;
import smu.nuda.domain.ingredient.validator.IngredientCsvValidator;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.ProductIngredient;
import smu.nuda.domain.product.entity.enums.CategoryCode;
import smu.nuda.domain.product.repository.CategoryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientAdminService {

    private final IngredientCsvReader csvReader;
    private final IngredientCsvValidator validator;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final int BATCH_SIZE = 500;
    @PersistenceContext private EntityManager em;

    @Transactional
    public CsvUploadResponse uploadIngredientsByCsv(MultipartFile file, boolean dryRun) {
        List<IngredientCsvRow> rows = csvReader.read(file);
        validator.validate(rows);

        persistInBatch(rows, dryRun);

        int total = rows.size();
        int success = dryRun ? 0 : total;

        return new CsvUploadResponse(total, success, 0);
    }

    private void persistInBatch(List<IngredientCsvRow> rows, boolean dryRun) {
        Map<String, Product> productMap = preloadProducts();
        Map<String, Category> categoryMap = preloadCategories();
        Map<String, Ingredient> ingredientCache = preloadIngredients();
        Set<String> mappingSet = preloadMappings();

        int count = 0;
        for (IngredientCsvRow row : rows) {
            Category category = findCategory(categoryMap, row);
            Product product = findProduct(productMap, row, category.getCode());
            LayerType layerType = findLayerType(row);

            Ingredient ingredient = resolveIngredient(ingredientCache, row, layerType, dryRun);
            processMapping(product, ingredient, mappingSet, row, dryRun);

            count++;

            if (!dryRun && count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }

        if (!dryRun) {
            em.flush();
            em.clear();
        }
    }


    private Map<String, Category> preloadCategories() {
        return categoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        category -> category.getCode().name(),
                        Function.identity()
                ));
    }

    private Map<String, Product> preloadProducts() {
        return productRepository.findAllWithCategory()
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getExternalProductId() + "_" + p.getCategory().getCode(),
                        Function.identity()
                ));
    }

    private Map<String, Ingredient> preloadIngredients() {
        return em.createQuery("""
                select i from Ingredient i
                """, Ingredient.class)
                .getResultStream()
                .collect(Collectors.toMap(
                        i -> i.getName() + "_" + i.getLayerType().name(),
                        Function.identity()
                ));
    }

    private Set<String> preloadMappings() {
        return em.createQuery("""
                select concat(pi.product.id, '_', pi.ingredient.id)
                from ProductIngredient pi
                """, String.class)
                .getResultStream()
                .collect(Collectors.toSet());
    }

    private Category findCategory(Map<String, Category> categoryMap, IngredientCsvRow row) {
        Category category = categoryMap.get(row.categoryCode());
        if (category == null) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_REFERENCE,
                    row.rowNumber(),
                    "존재하지 않는 category_code 입니다."
            );
        }

        return category;
    }

    private Product findProduct(Map<String, Product> map, IngredientCsvRow row, CategoryCode categoryCode) {
        String key = row.externalProductId() + "_" + categoryCode.name();
        Product product = map.get(key);

        if (product == null) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_REFERENCE,
                    row.rowNumber(),
                    "매핑되는 상품이 존재하지 않습니다."
            );
        }

        return product;
    }

    private LayerType findLayerType(IngredientCsvRow row) {
        try {
            return LayerType.valueOf(row.layerGroup());
        } catch (IllegalArgumentException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    row.rowNumber(),
                    "존재하지 않는 layer_group 입니다."
            );
        }
    }

    private Ingredient resolveIngredient(Map<String, Ingredient> ingredientCache, IngredientCsvRow row, LayerType layerType, boolean dryRun) {
        String key = row.subMaterial() + "_" + layerType.name();
        Ingredient ingredient = ingredientCache.get(key);
        if (ingredient != null) return ingredient;

        ingredient = Ingredient.create(
                row.subMaterial(),
                RiskLevel.UNKNOWN,
                layerType,
                row.content()
        );

        if (!dryRun) {
            em.persist(ingredient);
        }

        ingredientCache.put(key, ingredient);

        return ingredient;
    }

    private void processMapping(Product product, Ingredient ingredient, Set<String> mappingSet, IngredientCsvRow row, boolean dryRun) {
        String mappingKey = product.getId() + "_" + ingredient.getId();
        if (mappingSet.contains(mappingKey)) return;

        if (!dryRun) {
            ProductIngredient mapping = ProductIngredient.create(
                    product,
                    ingredient,
                    row.externalProductId()
            );

            em.persist(mapping);
        }

        mappingSet.add(mappingKey);
    }

}
