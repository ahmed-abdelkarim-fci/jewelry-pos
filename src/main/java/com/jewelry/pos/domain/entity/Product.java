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

    private String modelName;

    @Enumerated(EnumType.STRING)
    private Purity purity;

    @Column(precision = 10, scale = 3)
    private BigDecimal grossWeight;

    @Column(precision = 10, scale = 2)
    private BigDecimal makingCharge;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;

    @Column(nullable = false)
    private BigDecimal costPrice; // Cost of acquisition (Gold + Labor)
}