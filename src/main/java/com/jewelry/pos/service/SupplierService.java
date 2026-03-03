package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Supplier;
import com.jewelry.pos.domain.repository.SupplierRepository;
import com.jewelry.pos.web.dto.SupplierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public Supplier createSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.name());
        supplier.setPhoneNumber(dto.phoneNumber());
        supplier.setAddress(dto.address());
        supplier.setNotes(dto.notes());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(String id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        supplier.setName(dto.name());
        supplier.setPhoneNumber(dto.phoneNumber());
        supplier.setAddress(dto.address());
        supplier.setNotes(dto.notes());
        return supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(String id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
    }

    @Transactional
    public void deleteSupplier(String id) {
        supplierRepository.deleteById(id);
    }
}
