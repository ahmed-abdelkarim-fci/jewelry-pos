package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scrap_purification")
@Getter @Setter
public class ScrapPurification extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(nullable = false, length = 10)
    private String karat;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weightOut;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cashReceived;

    private String factoryName;
}