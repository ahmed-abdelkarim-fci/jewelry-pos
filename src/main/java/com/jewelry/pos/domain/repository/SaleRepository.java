package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.math.BigDecimal;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String>, JpaSpecificationExecutor<Sale> {
    @Query("SELECT s FROM Sale s WHERE s.transactionDate BETWEEN :start AND :end")
    List<Sale> findSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Sum of sales between two dates
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.transactionDate BETWEEN :start AND :end")
    BigDecimal sumTotalSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Count of sales today
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.transactionDate BETWEEN :start AND :end")
    long countSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Add this inside the interface
    List<Sale> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}