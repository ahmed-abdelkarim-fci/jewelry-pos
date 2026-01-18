package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "gold_rate")
public class GoldRate extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(name = "rate_24k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate24k;

    @Column(name = "rate_21k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate21k;

    @Column(name = "rate_18k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate18k;

    @Column(nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "is_active")
    private boolean active = true;
}