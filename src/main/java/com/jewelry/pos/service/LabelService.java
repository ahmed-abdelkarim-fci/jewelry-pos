package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final ProductRepository productRepository;

    public String generateZplLabel(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        /*
           ZPL Logic Explained:
           ^XA : Start Label
           ^FO : Field Origin (X,Y coordinates)
           ^BC : Code 128 Barcode
           ^FD : Field Data (The content)
           ^FS : Field Separator (End of line)
           ^XZ : End Label
        */
        
        return "^XA" +
               "^FO50,50^ADN,36,20^FD" + product.getModelName() + "^FS" +
               "^FO50,100^ADN,36,20^FD" + product.getGrossWeight() + "g^FS" +
               "^FO50,150^BCN,100,Y,N,N^FD" + product.getBarcode() + "^FS" +
               "^XZ";
    }
}