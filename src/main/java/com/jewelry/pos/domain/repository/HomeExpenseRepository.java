package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.HomeExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeExpenseRepository extends JpaRepository<HomeExpense, String> {
    
    List<HomeExpense> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
