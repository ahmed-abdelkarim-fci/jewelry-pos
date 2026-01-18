package com.jewelry.pos.domain.repository;
import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.domain.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByBarcode(String barcode);
    long countByStatus(ProductStatus status);
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.modelName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR p.barcode LIKE CONCAT('%', :query, '%')) " +
            "AND p.status = 'AVAILABLE'")
    List<Product> searchAvailableProducts(@Param("query") String query);
}