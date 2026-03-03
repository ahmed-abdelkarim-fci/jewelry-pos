package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.HomeExpense;
import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import com.jewelry.pos.domain.repository.HomeExpenseRepository;
import com.jewelry.pos.web.dto.HomeExpenseRequestDTO;
import com.jewelry.pos.web.dto.HomeExpenseSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeExpenseService {

    private final HomeExpenseRepository homeExpenseRepository;

    @Transactional
    public HomeExpense createTransaction(HomeExpenseRequestDTO dto) {
        HomeExpense expense = new HomeExpense();
        expense.setTransactionDate(dto.transactionDate());
        expense.setDescription(dto.description());
        expense.setTransactionType(dto.transactionType());
        expense.setWeight(dto.weight() != null ? dto.weight() : BigDecimal.ZERO);
        expense.setMoney(dto.money() != null ? dto.money() : BigDecimal.ZERO);
        return homeExpenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<HomeExpense> getAllTransactions(Pageable pageable) {
        return homeExpenseRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public HomeExpenseSummaryDTO getSummary() {
        List<HomeExpense> allExpenses = homeExpenseRepository.findAll();
        
        BigDecimal totalMoneyReceivable = BigDecimal.ZERO;
        BigDecimal totalMoneyPayable = BigDecimal.ZERO;
        BigDecimal totalWeightReceivable = BigDecimal.ZERO;
        BigDecimal totalWeightPayable = BigDecimal.ZERO;
        
        for (HomeExpense expense : allExpenses) {
            if (expense.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                totalMoneyReceivable = totalMoneyReceivable.add(expense.getMoney());
                totalWeightReceivable = totalWeightReceivable.add(expense.getWeight());
            } else {
                totalMoneyPayable = totalMoneyPayable.add(expense.getMoney());
                totalWeightPayable = totalWeightPayable.add(expense.getWeight());
            }
        }
        
        BigDecimal netMoney = totalMoneyReceivable.subtract(totalMoneyPayable);
        BigDecimal netWeight = totalWeightReceivable.subtract(totalWeightPayable);
        
        return new HomeExpenseSummaryDTO(
            totalMoneyReceivable,
            totalMoneyPayable,
            totalWeightReceivable,
            totalWeightPayable,
            netMoney,
            netWeight,
            allExpenses.size()
        );
    }

    @Transactional
    public void deleteTransaction(String id) {
        homeExpenseRepository.deleteById(id);
    }
}
