package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "sale")
public class Sale extends Auditable {

    @Id @Tsid @Column(length = 26)
    private String id;

    private String customerName;
    private String customerPhone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    // The Gross Total (Value of new items being sold)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    // --- NEW FIELDS FOR OLD GOLD (TRADE-IN) ---

    // Value deducted due to trading in old gold
    @Column(precision = 12, scale = 2)
    private BigDecimal oldGoldTotalValue = BigDecimal.ZERO;

    // The actual cash the customer paid (Total - OldGold)
    @Column(precision = 12, scale = 2)
    private BigDecimal netCashPaid = BigDecimal.ZERO;

    // ------------------------------------------

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }
}