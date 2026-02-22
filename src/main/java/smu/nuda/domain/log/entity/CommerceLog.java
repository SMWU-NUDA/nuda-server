package smu.nuda.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import smu.nuda.domain.log.entity.enums.CommerceType;

import java.time.LocalDateTime;

@Entity
@Table(name = "rec_event_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommerceLog {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rec_event_log_seq_generator")
    @SequenceGenerator(
            name = "rec_event_log_seq_generator",
            sequenceName = "rec_event_log_seq",
            allocationSize = 10
    )
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "external_product_id", nullable = false)
    private String externalProductId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "event_type", nullable = false, columnDefinition = "rec_event_type")
    private CommerceType commerceType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

}
