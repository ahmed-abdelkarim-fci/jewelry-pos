package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.PersonalAccount;
import com.jewelry.pos.domain.entity.PersonalPerson;
import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import com.jewelry.pos.domain.repository.PersonalAccountRepository;
import com.jewelry.pos.domain.repository.PersonalPersonRepository;
import com.jewelry.pos.web.dto.PersonalAccountRequestDTO;
import com.jewelry.pos.web.dto.PersonalAccountSummaryDTO;
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
public class PersonalAccountService {

    private final PersonalAccountRepository personalAccountRepository;
    private final PersonalPersonRepository personalPersonRepository;

    @Transactional
    public PersonalAccount createTransaction(PersonalAccountRequestDTO dto) {
        PersonalAccount account = new PersonalAccount();
        account.setPersonId(dto.personId());
        account.setTransactionDate(dto.transactionDate());
        account.setStatement(dto.statement());
        account.setTransactionType(dto.transactionType());
        account.setWeight(dto.weight() != null ? dto.weight() : BigDecimal.ZERO);
        account.setMoney(dto.money() != null ? dto.money() : BigDecimal.ZERO);
        return personalAccountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Page<PersonalAccount> getAllTransactions(Pageable pageable) {
        return personalAccountRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<PersonalAccount> getTransactionsByPerson(String personId, Pageable pageable) {
        return personalAccountRepository.findByPersonId(personId, pageable);
    }

    @Transactional(readOnly = true)
    public List<PersonalAccountSummaryDTO> getPersonSummaries() {
        List<PersonalPerson> persons = personalPersonRepository.findAll();

        return persons.stream().map(person -> {
            List<PersonalAccount> transactions = personalAccountRepository.findByPersonId(person.getId());
            
            BigDecimal netMoney = BigDecimal.ZERO;
            BigDecimal netWeight = BigDecimal.ZERO;
            
            for (PersonalAccount tx : transactions) {
                if (tx.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                    netMoney = netMoney.add(tx.getMoney());
                    netWeight = netWeight.add(tx.getWeight());
                } else {
                    netMoney = netMoney.subtract(tx.getMoney());
                    netWeight = netWeight.subtract(tx.getWeight());
                }
            }
            
            String moneyStatus = netMoney.compareTo(BigDecimal.ZERO) > 0 ? "RECEIVABLE" : 
                                netMoney.compareTo(BigDecimal.ZERO) < 0 ? "PAYABLE" : "SETTLED";
            String weightStatus = netWeight.compareTo(BigDecimal.ZERO) > 0 ? "RECEIVABLE" : 
                                 netWeight.compareTo(BigDecimal.ZERO) < 0 ? "PAYABLE" : "SETTLED";
            
            return new PersonalAccountSummaryDTO(
                person.getId(),
                person.getName(),
                netMoney,
                netWeight,
                moneyStatus,
                weightStatus,
                transactions.size()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTransaction(String id) {
        personalAccountRepository.deleteById(id);
    }
}
