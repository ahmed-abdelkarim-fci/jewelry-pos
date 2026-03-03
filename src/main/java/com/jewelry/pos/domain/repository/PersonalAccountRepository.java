package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.PersonalAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, String> {
    
    Page<PersonalAccount> findByPersonId(String personId, Pageable pageable);

    List<PersonalAccount> findByPersonId(String personId);
    
    List<PersonalAccount> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
