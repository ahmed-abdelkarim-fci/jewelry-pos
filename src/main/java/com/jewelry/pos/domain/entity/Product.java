package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@Table(name = "product", indexes = @Index(columnList = "barcode"))
@EntityListeners(AuditingEntityListener.class)
public class Product extends Auditable {
    @Id @Tsid @Column(length = 26)
    private String id;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false)
    private String modelName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurityEnum purityEnum; // العيار

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JewelryTypeEnum type;

    @Column(precision = 10, scale = 3)
    private BigDecimal grossWeight; // الوزن

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal makingCharge; // المصنعيه

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatusEnum status = ProductStatusEnum.AVAILABLE;

    // السعر اثناء الشراء التلفة الكلية و العماله
    @Column(nullable = false)
    private BigDecimal costPrice; // Cost of acquisition (Gold + Labor)

    @Version
    private Integer version;

}