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
@Table(name = "personal_account")
@Getter @Setter
public class PersonalAccount extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(nullable = false, length = 26)
    private String personId;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(nullable = false, length = 1000)
    private String statement;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(precision = 12, scale = 2)
    private BigDecimal money;
}
