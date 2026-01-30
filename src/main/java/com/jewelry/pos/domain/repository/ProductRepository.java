package com.jewelry.pos.domain.repository;
import com.jewelry.pos.domain.entity.JewelryTypeEnum;
import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.domain.entity.ProductStatusEnum;
import com.jewelry.pos.domain.entity.PurityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByBarcode(String barcode);
    long countByStatus(ProductStatusEnum status);
    
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.modelName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR p.barcode LIKE CONCAT('%', :query, '%')) " +
            "AND p.status = 'AVAILABLE'")
    List<Product> searchAvailableProducts(@Param("query") String query);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'AVAILABLE' " +
            "AND (:query IS NULL OR :query = '' OR " +
            "    p.barcode LIKE CONCAT('%', :query, '%') OR " +
            "    LOWER(p.modelName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "    LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:purity IS NULL OR p.purityEnum = :purity) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:minWeight IS NULL OR p.grossWeight >= :minWeight) " +
            "AND (:maxWeight IS NULL OR p.grossWeight <= :maxWeight) " +
            "ORDER BY " +
            "CASE WHEN p.barcode LIKE CONCAT('%', :query, '%') THEN 1 " +
            "     WHEN LOWER(p.modelName) LIKE LOWER(CONCAT('%', :query, '%')) THEN 2 " +
            "     ELSE 3 END, p.modelName")
    List<Product> searchProductsWithFilters(
        @Param("query") String query,
        @Param("purity") PurityEnum purity,
        @Param("type") JewelryTypeEnum type,
        @Param("minWeight") BigDecimal minWeight,
        @Param("maxWeight") BigDecimal maxWeight
    );
}