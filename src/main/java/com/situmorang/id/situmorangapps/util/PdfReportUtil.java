package com.situmorang.id.situmorangapps.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.situmorang.id.situmorangapps.model.Sales;
import com.situmorang.id.situmorangapps.model.SalesItem;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PdfReportUtil {

    public static void generateSalesReport(List<Sales> salesList, String filePath) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);

            Paragraph title = new Paragraph("Laporan Penjualan", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            PdfPTable table = new PdfPTable(8);
            table.setWidths(new float[]{1f, 3f, 3f, 1f, 2f, 2f, 2f, 2f});
            table.setWidthPercentage(100f);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            // Header
            table.addCell(createPdfCell("No", Element.ALIGN_CENTER, true));
            table.addCell(createPdfCell("Tanggal", Element.ALIGN_CENTER, true));
            table.addCell(createPdfCell("Produk", Element.ALIGN_LEFT, true));
            table.addCell(createPdfCell("Qty", Element.ALIGN_CENTER, true));
            table.addCell(createPdfCell("Harga Jual", Element.ALIGN_RIGHT, true));
            table.addCell(createPdfCell("Harga Beli", Element.ALIGN_RIGHT, true));
            table.addCell(createPdfCell("Subtotal", Element.ALIGN_RIGHT, true));
            table.addCell(createPdfCell("Keuntungan", Element.ALIGN_RIGHT, true));

            double grandTotal = 0;
            double grandProfit = 0;
            int rowNumber = 1;

            for (Sales sale : salesList) {
                String formattedDateTime = sale.getDate() != null
                        ? sale.getDate().format(dateTimeFormatter) : "-";

                for (SalesItem item : sale.getItems()) {
                    double hargaJual = item.getPrice();
                    double hargaBeli = item.getProduct().getPriceIn();
                    int qty = item.getQuantity();
                    double subtotal = hargaJual * qty;
                    double profit = (hargaJual - hargaBeli) * qty;

                    grandTotal += subtotal;
                    grandProfit += profit;

                    table.addCell(createPdfCell(String.valueOf(rowNumber++), Element.ALIGN_CENTER, false));
                    table.addCell(createPdfCell(formattedDateTime, Element.ALIGN_CENTER, false));
                    table.addCell(createPdfCell(item.getProduct().getName(), Element.ALIGN_LEFT, false));
                    table.addCell(createPdfCell(String.valueOf(qty), Element.ALIGN_CENTER, false));
                    table.addCell(createPdfCell(rupiah.format(hargaJual), Element.ALIGN_RIGHT, false));
                    table.addCell(createPdfCell(rupiah.format(hargaBeli), Element.ALIGN_RIGHT, false));
                    table.addCell(createPdfCell(rupiah.format(subtotal), Element.ALIGN_RIGHT, false));
                    table.addCell(createPdfCell(rupiah.format(profit), Element.ALIGN_RIGHT, false));
                }
            }

            document.add(table);

            document.add(new Paragraph("Total: " + rupiah.format(grandTotal), normalFont));
            document.add(new Paragraph("Keuntungan: " + rupiah.format(grandProfit), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Dicetak oleh: Situmorang Kasir Apps", footerFont));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private static PdfPCell createPdfCell(String content, int alignment, boolean bold) {
        Font font = bold
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)
                : FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        return cell;
    }
}
