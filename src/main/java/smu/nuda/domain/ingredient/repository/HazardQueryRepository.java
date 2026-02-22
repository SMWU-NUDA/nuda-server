package smu.nuda.domain.ingredient.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HazardQueryRepository {

    private final EntityManager em;

    public String findExtractedJson(Long ingredientId) {

        String sql = """
            SELECT h.extracted
            FROM ingredient_normalized n
            JOIN ingredient_msds_mapping m 
                ON n.id = m.ingredient_normalized_id
            JOIN msds_hazard h 
                ON h.candidate_id = m.chosen_candidate_id
            WHERE n.ingredient_id = :ingredientId
            LIMIT 1
        """;

        List<?> result = em.createNativeQuery(sql)
                .setParameter("ingredientId", ingredientId)
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0).toString();
    }
}
