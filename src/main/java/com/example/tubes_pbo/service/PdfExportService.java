package com.example.tubes_pbo.service;

import com.example.tubes_pbo.model.Mahasiswa;
import com.example.tubes_pbo.model.Nilai;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateTranscriptPdf(Mahasiswa mahasiswa, List<Nilai> nilaiList) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            // Add Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph header = new Paragraph("TRANSKRIP NILAI MAHASISWA", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Add Student Info
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);
            infoTable.setWidths(new float[]{1, 3});

            addInfoRow(infoTable, "NIM", mahasiswa.getNim(), labelFont, valueFont);
            addInfoRow(infoTable, "Nama", mahasiswa.getNama(), labelFont, valueFont);
            addInfoRow(infoTable, "Tanggal Cetak", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")), labelFont, valueFont);

            document.add(infoTable);

            // Add Grades Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{1, 3, 1.5f, 1.5f, 1.5f, 1.5f});

            // Table Header
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            addTableHeader(table, "No", tableHeaderFont);
            addTableHeader(table, "Mata Kuliah", tableHeaderFont);
            addTableHeader(table, "Tugas", tableHeaderFont);
            addTableHeader(table, "UTS", tableHeaderFont);
            addTableHeader(table, "UAS", tableHeaderFont);
            addTableHeader(table, "Grade", tableHeaderFont);

            // Table Content
            Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            int no = 1;
            double totalRata = 0;

            for (Nilai nilai : nilaiList) {
                addTableCell(table, String.valueOf(no++), tableCellFont, Element.ALIGN_CENTER);
                addTableCell(table, nilai.getMataKuliah(), tableCellFont, Element.ALIGN_LEFT);
                addTableCell(table, String.format("%.1f", nilai.getTugas()), tableCellFont, Element.ALIGN_CENTER);
                addTableCell(table, String.format("%.1f", nilai.getUts()), tableCellFont, Element.ALIGN_CENTER);
                addTableCell(table, String.format("%.1f", nilai.getUas()), tableCellFont, Element.ALIGN_CENTER);
                
                PdfPCell gradeCell = new PdfPCell(new Phrase(nilai.getGrade(), tableCellFont));
                gradeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                gradeCell.setPadding(5);
                gradeCell.setBackgroundColor(getGradeColor(nilai.getGrade()));
                table.addCell(gradeCell);

                totalRata += nilai.hitungRataRata();
            }

            // Add Average Row
            PdfPCell avgLabelCell = new PdfPCell(new Phrase("Rata-rata Keseluruhan", labelFont));
            avgLabelCell.setColspan(5);
            avgLabelCell.setPadding(8);
            avgLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            avgLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(avgLabelCell);

            double rata = nilaiList.isEmpty() ? 0 : totalRata / nilaiList.size();
            PdfPCell avgValueCell = new PdfPCell(new Phrase(String.format("%.2f", rata), labelFont));
            avgValueCell.setPadding(8);
            avgValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            avgValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(avgValueCell);

            document.add(table);

            // Add Footer
            Paragraph footer = new Paragraph("\nDokumen ini dicetak secara otomatis oleh sistem.", 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(": " + value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(102, 126, 234)); // Purple-blue color
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorderWidth(1);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private BaseColor getGradeColor(String grade) {
        return switch (grade) {
            case "A" -> new BaseColor(40, 167, 69);   // Green
            case "B" -> new BaseColor(0, 123, 255);   // Blue
            case "C" -> new BaseColor(23, 162, 184);  // Cyan
            case "D" -> new BaseColor(255, 193, 7);   // Yellow
            default -> new BaseColor(220, 53, 69);    // Red
        };
    }
}

