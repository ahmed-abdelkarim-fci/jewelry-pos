package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.SupplierAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierAccountRepository extends JpaRepository<SupplierAccount, String> {
    
    Page<SupplierAccount> findBySupplierId(String supplierId, Pageable pageable);
    
    List<SupplierAccount> findBySupplierId(String supplierId);
    
    List<SupplierAccount> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
