package com.jewelry.pos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "scrap_inventory")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapInventory {

    @Id
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private KaratEnum purity; // KARAT_21, KARAT_18

    //وزن الكسر اللي موجود لكل قيراط
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal totalWeight;
}