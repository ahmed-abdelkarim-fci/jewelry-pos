package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter @Setter
@Table(name = "sale_item")
public class SaleItem extends Auditable {
    @Id @Tsid @Column(length = 26)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedGoldRate;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weightSnapshot;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSnapshot;
}