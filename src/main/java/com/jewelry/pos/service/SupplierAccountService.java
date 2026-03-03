package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Supplier;
import com.jewelry.pos.domain.entity.SupplierAccount;
import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import com.jewelry.pos.domain.repository.SupplierAccountRepository;
import com.jewelry.pos.domain.repository.SupplierRepository;
import com.jewelry.pos.web.dto.SupplierAccountRequestDTO;
import com.jewelry.pos.web.dto.SupplierAccountSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierAccountService {

    private final SupplierAccountRepository supplierAccountRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierAccount createTransaction(SupplierAccountRequestDTO dto) {
        SupplierAccount account = new SupplierAccount();
        account.setSupplierId(dto.supplierId());
        account.setTransactionDate(dto.transactionDate());
        account.setStatement(dto.statement());
        account.setTransactionType(dto.transactionType());
        account.setWeight(dto.weight() != null ? dto.weight() : BigDecimal.ZERO);
        account.setFees(dto.fees() != null ? dto.fees() : BigDecimal.ZERO);
        account.setNumberOfPieces(dto.numberOfPieces());
        account.setPurificationId(dto.purificationId());
        return supplierAccountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Page<SupplierAccount> getAllTransactions(Pageable pageable) {
        return supplierAccountRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SupplierAccount> getTransactionsBySupplier(String supplierId, Pageable pageable) {
        return supplierAccountRepository.findBySupplierId(supplierId, pageable);
    }

    @Transactional(readOnly = true)
    public List<SupplierAccountSummaryDTO> getSupplierSummaries() {
        List<Supplier> suppliers = supplierRepository.findAll();
        
        return suppliers.stream().map(supplier -> {
            List<SupplierAccount> transactions = supplierAccountRepository.findBySupplierId(supplier.getId());
            
            BigDecimal netFees = BigDecimal.ZERO;
            BigDecimal netWeight = BigDecimal.ZERO;
            
            for (SupplierAccount tx : transactions) {
                if (tx.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                    netFees = netFees.add(tx.getFees());
                    netWeight = netWeight.add(tx.getWeight());
                } else {
                    netFees = netFees.subtract(tx.getFees());
                    netWeight = netWeight.subtract(tx.getWeight());
                }
            }
            
            String feesStatus = netFees.compareTo(BigDecimal.ZERO) > 0 ? "RECEIVABLE" : 
                               netFees.compareTo(BigDecimal.ZERO) < 0 ? "PAYABLE" : "SETTLED";
            String weightStatus = netWeight.compareTo(BigDecimal.ZERO) > 0 ? "RECEIVABLE" : 
                                 netWeight.compareTo(BigDecimal.ZERO) < 0 ? "PAYABLE" : "SETTLED";
            
            return new SupplierAccountSummaryDTO(
                supplier.getId(),
                supplier.getName(),
                netFees,
                netWeight,
                feesStatus,
                weightStatus,
                transactions.size()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTransaction(String id) {
        supplierAccountRepository.deleteById(id);
    }
}
