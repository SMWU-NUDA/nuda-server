package smu.nuda.domain.search.document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Getter
@NoArgsConstructor
@Document(indexName = "products")
@Setting(settingPath = "elasticsearch/product-settings.json")
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long productId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori_search"),
            otherFields = {
                    @InnerField(suffix = "prefix", type = FieldType.Text, analyzer = "product_edge_ngram", searchAnalyzer = "standard")
            }
    )
    private String productName;

    @Field(type = FieldType.Text, analyzer = "ingredient_ngram", searchAnalyzer = "standard")
    private List<String> ingredientNames;

    @MultiField(
            mainField = @Field(type = FieldType.Keyword),
            otherFields = {
                    @InnerField(suffix = "text", type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori_search")
            }
    )
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String thumbnailImg;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Double)
    private double averageRating;

    @Field(type = FieldType.Integer)
    private int reviewCount;

    @Field(type = FieldType.Integer)
    private int likeCount;

    @Field(type = FieldType.Integer)
    private int costPrice;

    @Builder
    public ProductDocument(String id, Long productId, String productName, List<String> ingredientNames,
                           String brandName, String thumbnailImg, Long brandId,
                           double averageRating, int reviewCount, int likeCount, int costPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.ingredientNames = ingredientNames != null ? ingredientNames : List.of();
        this.brandName = brandName;
        this.thumbnailImg = thumbnailImg;
        this.brandId = brandId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.likeCount = likeCount;
        this.costPrice = costPrice;
    }
}
