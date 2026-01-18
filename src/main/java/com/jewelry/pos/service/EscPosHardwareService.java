package com.jewelry.pos.service;

import com.jewelry.pos.service.HardwareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@Profile("prod")
public class EscPosHardwareService implements HardwareService {

    // Common ESC/POS Command to trigger Drawer Kick (Pin 2, 100ms on, 200ms off)
    // ASCII: ESC p 0 25 250
    private static final byte[] OPEN_DRAWER_CMD = {0x1B, 0x70, 0x00, 0x19, (byte) 0xFA};
    
    // Cut Paper Command (GS V 66 0)
    private static final byte[] CUT_PAPER_CMD = {0x1D, 0x56, 0x42, 0x00};

    @Override
    public void openCashDrawer() {
        log.info("Attempting to open cash drawer via Default Printer...");
        sendBytesToDefaultPrinter(OPEN_DRAWER_CMD);
    }

    @Override
    public void printReceipt(String content) {
        log.info("Sending receipt to printer...");
        
        // Convert text to bytes and append the "Cut Paper" command
        byte[] textBytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[textBytes.length + CUT_PAPER_CMD.length];
        
        System.arraycopy(textBytes, 0, combined, 0, textBytes.length);
        System.arraycopy(CUT_PAPER_CMD, 0, combined, textBytes.length, CUT_PAPER_CMD.length);

        sendBytesToDefaultPrinter(combined);
    }

    private void sendBytesToDefaultPrinter(byte[] data) {
        // 1. Find the default printer service (The OS default printer)
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();

        if (service == null) {
            log.error("CRITICAL: No default printer found! Cannot open drawer or print.");
            // In production, you might throw a custom RuntimeException here to alert the UI
            return; 
        }

        try {
            // 2. Create a print job
            DocPrintJob job = service.createPrintJob();
            
            // 3. Define the document type as raw bytes (AUTOSENSE allows raw command passthrough)
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(data, flavor, null);
            
            // 4. Print
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            job.print(doc, attrs);
            
        } catch (PrintException e) {
            log.error("Hardware Error: Failed to send command to printer.", e);
            // Don't rethrow if you want the sale to persist even if printing fails
            // Or throw specific HardwareException if you want to notify the frontend
        }
    }
}