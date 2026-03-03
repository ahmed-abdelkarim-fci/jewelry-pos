package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_expense")
@Getter @Setter
public class HomeExpense extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(precision = 12, scale = 2)
    private BigDecimal money;
}
