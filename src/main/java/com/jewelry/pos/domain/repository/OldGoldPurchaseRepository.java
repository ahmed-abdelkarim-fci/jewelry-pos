package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.OldGoldPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OldGoldPurchaseRepository extends JpaRepository<OldGoldPurchase, String> {
    List<OldGoldPurchase> findAllBySaleId(String saleId);
}