package com.example.demo.services;

import Fines.FineType;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class PdfGenerator {

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private PdfGenerator() {}

    public static Path generateFinePDF(
            Path outDir,
            String fineNumber,
            ViolationService.Violation v,
            FineType type,
            FineEmissionService.CalcResult calc,
            String address,
            String owner,
            String brand,
            String model,
            String color,
            String barcode,      // 6+12 dígitos
            String photoPathOrNull   // ahora puede ser ruta de classpath (empieza con "/")
    ) {
        Path out = outDir.resolve("fine-" + fineNumber + ".pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                // Header
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(margin, y);
                cs.showText("Traffic Department - Municipality");
                cs.endText();

                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Fine number:" + fineNumber + "   Issue date: " + TS_FMT.format(java.time.Instant.now()));
                cs.endText();

                // Datos del auto / titular
                y -= 28;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Vehicle data");
                cs.endText();

                y -= 16;
                writeLine(cs, margin, y, "Owner: " + owner);  y -= 14;
                writeLine(cs, margin, y, "Patent:" + v.plate); y -= 14;
                writeLine(cs, margin, y, "Brand/Model/Color: " + brand + " / " + model + " / " + color);

                // Infracción
                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Infractión");
                cs.endText();

                y -= 16;
                writeLine(cs, margin, y, "Type: " + type.getCode() + " - " + type.getDescription()); y -= 14;
                writeLine(cs, margin, y, "Device: " + v.deviceId + "   Date/Time: " + TS_FMT.format(v.ts)); y -= 14;
                writeLine(cs, margin, y, "Address: " + address); y -= 14;
                writeLine(cs, margin, y, "Details: " + v.details);

                // Foto (opcional) — ahora carga desde classpath o desde archivo, según lo que venga
                PDImageXObject img = loadImageFromResourceOrFile(doc, photoPathOrNull);
                if (img != null) {
                    float imgW = 260;
                    float ratio = img.getHeight() / (float) img.getWidth();
                    float imgH = imgW * ratio;
                    y -= (imgH + 12);
                    cs.drawImage(img, margin, y, imgW, imgH);
                } else {
                    y -= 16;
                }

                // Importe y puntos
                y -= 12;
                writeLineBold(cs, margin, y, String.format("Amount: $ %.2f Points to be deducted: %d", calc.amount(), calc.points()));

                // “Código de barras” (impresión del número 6+12)
                y -= 28;
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 18);
                cs.newLineAtOffset(margin, y);
                cs.showText(barcode);
                cs.endText();

                // Pie
                y -= 24;
                writeLine(cs, margin, y, "Please submit this document within 10 business days.");
            }

            doc.save(out.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }

        return out;
    }

    /** Intenta primero cargar como recurso de classpath (cuando la ruta empieza con "/"). Si no, intenta como archivo. */
    private static PDImageXObject loadImageFromResourceOrFile(PDDocument doc, String path) {
        if (path == null || path.isBlank()) return null;

        // 1) Classpath (resources)
        if (path.startsWith("/")) {
            try (InputStream is = PdfGenerator.class.getResourceAsStream(path)) {
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    return PDImageXObject.createFromByteArray(doc, bytes, "fine-photo");
                }
            } catch (IOException ignore) { /* si falla, probamos archivo */ }
        }

        // 2) Ruta de archivo (absoluta o relativa)
        try {
            return PDImageXObject.createFromFile(path, doc);
        } catch (IOException e) {
            return null; // no frenamos el PDF por una foto
        }
    }

    private static void writeLine(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static void writeLineBold(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }
}