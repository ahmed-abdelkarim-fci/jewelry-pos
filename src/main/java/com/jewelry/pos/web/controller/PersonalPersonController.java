package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.PersonalPerson;
import com.jewelry.pos.service.PersonalPersonService;
import com.jewelry.pos.web.dto.PersonalPersonDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal-persons")
@RequiredArgsConstructor
@Tag(name = "Personal Persons Management")
public class PersonalPersonController {

    private final PersonalPersonService personalPersonService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Create a new personal person")
    public ResponseEntity<PersonalPerson> createPerson(@Valid @RequestBody PersonalPersonDTO dto) {
        return ResponseEntity.ok(personalPersonService.createPerson(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Update personal person")
    public ResponseEntity<PersonalPerson> updatePerson(@PathVariable String id, @Valid @RequestBody PersonalPersonDTO dto) {
        return ResponseEntity.ok(personalPersonService.updatePerson(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all personal persons")
    public ResponseEntity<List<PersonalPerson>> getAllPersons() {
        return ResponseEntity.ok(personalPersonService.getAllPersons());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get personal person by ID")
    public ResponseEntity<PersonalPerson> getPersonById(@PathVariable String id) {
        return ResponseEntity.ok(personalPersonService.getPersonById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete personal person")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        personalPersonService.deletePerson(id);
        return ResponseEntity.ok().build();
    }
}
