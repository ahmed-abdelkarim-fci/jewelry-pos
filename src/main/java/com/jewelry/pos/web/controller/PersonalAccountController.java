package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.PersonalAccount;
import com.jewelry.pos.service.PersonalAccountService;
import com.jewelry.pos.web.dto.PersonalAccountRequestDTO;
import com.jewelry.pos.web.dto.PersonalAccountSummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal-accounts")
@RequiredArgsConstructor
@Tag(name = "Personal Accounts Management")
public class PersonalAccountController {

    private final PersonalAccountService personalAccountService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Create a new personal account transaction")
    public ResponseEntity<PersonalAccount> createTransaction(@Valid @RequestBody PersonalAccountRequestDTO dto) {
        return ResponseEntity.ok(personalAccountService.createTransaction(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all personal account transactions with pagination")
    public ResponseEntity<Page<PersonalAccount>> getAllTransactions(
            @PageableDefault(size = 20, sort = "lastModifiedDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(personalAccountService.getAllTransactions(pageable));
    }

    @GetMapping("/person/{personId}")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all transactions for a specific person")
    public ResponseEntity<Page<PersonalAccount>> getTransactionsByPerson(
            @PathVariable String personId,
            @PageableDefault(size = 20, sort = "lastModifiedDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(personalAccountService.getTransactionsByPerson(personId, pageable));
    }

    @GetMapping("/summaries")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get summary of all persons with net balances")
    public ResponseEntity<List<PersonalAccountSummaryDTO>> getPersonSummaries() {
        return ResponseEntity.ok(personalAccountService.getPersonSummaries());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete a personal account transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        personalAccountService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
