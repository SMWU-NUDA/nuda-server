package smu.nuda.domain.component.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.component.entity.enums.LayerType;

@Entity
@Table(name = "component")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "component_seq",
        sequenceName = "component_seq",
        allocationSize = 1
)
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "component_seq"
    )
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "layer_type", length = 30, nullable = false)
    private LayerType layerType;

    public static Component of(String name, String content, LayerType layerType) {
        Component component = new Component();
        component.name = name;
        component.content = content;
        component.layerType = layerType;
        return component;
    }
}
