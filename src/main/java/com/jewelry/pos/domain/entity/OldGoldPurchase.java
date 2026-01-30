package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "old_gold_purchase")
@Getter @Setter
public class OldGoldPurchase extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(nullable = false, length = 10)
    @Convert(converter = PurityEnumKaratConverter.class)
    private PurityEnum purity; // e.g., 'KARAT_21'

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal buyRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalValue;

    @Column(length = 26)
    private String saleId; // Null if it's a direct cash buy

    @Column(length = 50)
    private String customerNationalId;

    private String customerPhoneNumber;

    private String description;
}