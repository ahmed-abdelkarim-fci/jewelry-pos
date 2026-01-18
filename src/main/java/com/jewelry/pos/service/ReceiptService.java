package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Sale;
import com.jewelry.pos.domain.entity.SaleItem;
import com.jewelry.pos.domain.repository.SaleRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final SaleRepository saleRepository;

    // --- 1. Customer Receipt ---
    public byte[] generateReceiptPdf(String saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("JEWELRY SHOP RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Info
            document.add(new Paragraph("Bill #: " + sale.getId()));
            document.add(new Paragraph("Date: " + sale.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            document.add(new Paragraph("Customer: " + (sale.getCustomerName() != null ? sale.getCustomerName() : "Walk-in")));
            document.add(new Paragraph(" "));

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Item");
            table.addCell("Weight (g)");
            table.addCell("Rate");
            table.addCell("Price");

            for (SaleItem item : sale.getItems()) {
                table.addCell(item.getProduct().getModelName());
                table.addCell(item.getWeightSnapshot().toString());
                table.addCell(item.getAppliedGoldRate().toString());
                table.addCell(item.getPriceSnapshot().toString());
            }
            document.add(table);

            // Totals
            document.add(new Paragraph(" "));

            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(50);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.setWidths(new float[]{1.5f, 1});

            addTotalRow(totalsTable, "Subtotal:", sale.getTotalAmount() + " EGP", false);

            if (sale.getOldGoldTotalValue() != null && sale.getOldGoldTotalValue().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRow(totalsTable, "Less Old Gold:", "-" + sale.getOldGoldTotalValue() + " EGP", false);

                PdfPCell line = new PdfPCell(new Phrase(" "));
                line.setColspan(2);
                line.setBorder(Rectangle.BOTTOM);
                totalsTable.addCell(line);

                addTotalRow(totalsTable, "NET TO PAY:", sale.getNetCashPaid() + " EGP", true);
            } else {
                addTotalRow(totalsTable, "TOTAL:", sale.getTotalAmount() + " EGP", true);
            }

            document.add(totalsTable);

            // Footer
            document.add(new Paragraph(" "));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Paragraph footer = new Paragraph("Sold items cannot be returned, only exchanged within 14 days.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating receipt", e);
        }
    }

    // --- 2. Z-Report (End of Day Summary) ---
    public byte[] generateZReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findAllByTransactionDateBetween(start, end);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalNetCash = BigDecimal.ZERO;
        BigDecimal totalOldGoldIn = BigDecimal.ZERO;

        Map<String, BigDecimal> weightByKarat = new HashMap<>();

        for (Sale sale : sales) {
            totalRevenue = totalRevenue.add(sale.getTotalAmount());

            if (sale.getNetCashPaid() != null) {
                totalNetCash = totalNetCash.add(sale.getNetCashPaid());
            } else {
                totalNetCash = totalNetCash.add(sale.getTotalAmount());
            }

            if (sale.getOldGoldTotalValue() != null) {
                totalOldGoldIn = totalOldGoldIn.add(sale.getOldGoldTotalValue());
            }

            for (SaleItem item : sale.getItems()) {
                // --- FIX IS HERE: Added .name() ---
                String purity = (item.getProduct().getPurity() != null) ? item.getProduct().getPurity().name() : "UNKNOWN";
                BigDecimal weight = item.getWeightSnapshot();
                weightByKarat.merge(purity, weight, BigDecimal::add);
            }
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph header = new Paragraph("Z-REPORT (END OF DAY)", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph subHeader = new Paragraph("Date: " + date.format(DateTimeFormatter.ISO_DATE), subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);
            document.add(new Paragraph(" "));

            // Financial Table
            PdfPTable financeTable = new PdfPTable(2);
            financeTable.setWidthPercentage(100);
            financeTable.addCell(new PdfPCell(new Phrase("Metric", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            financeTable.addCell(new PdfPCell(new Phrase("Value", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

            financeTable.addCell("Total Sales (Revenue)");
            financeTable.addCell(totalRevenue + " EGP");

            financeTable.addCell("Less: Old Gold Received");
            financeTable.addCell("-" + totalOldGoldIn + " EGP");

            financeTable.addCell("Net Cash In Drawer");
            financeTable.addCell(totalNetCash + " EGP");

            financeTable.addCell("Total Transactions");
            financeTable.addCell(String.valueOf(sales.size()));

            document.add(financeTable);
            document.add(new Paragraph(" "));

            // Weight Table
            Paragraph inventoryTitle = new Paragraph("Gold Weight Sold by Karat", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(inventoryTitle);
            document.add(new Paragraph(" "));

            PdfPTable weightTable = new PdfPTable(2);
            weightTable.setWidthPercentage(100);
            weightTable.addCell("Karat / Purity");
            weightTable.addCell("Total Weight (grams)");

            if (weightByKarat.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No items sold today"));
                emptyCell.setColspan(2);
                weightTable.addCell(emptyCell);
            } else {
                for (Map.Entry<String, BigDecimal> entry : weightByKarat.entrySet()) {
                    weightTable.addCell(entry.getKey());
                    weightTable.addCell(entry.getValue() + " g");
                }
            }
            document.add(weightTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated automatically by Jewelry POS System."));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Z-Report", e);
        }
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean isBold) {
        Font font = isBold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12) : FontFactory.getFont(FontFactory.HELVETICA, 12);

        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
}