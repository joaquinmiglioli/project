package com.example.demo.services;

import Fines.FineType;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service; //aaaaaaaa

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
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
            String barcode,
            String photoFileName   // 👈 SOLO el nombre del archivo, ej: "CameraPhoto.png"
    ) {
        Path out = outDir.resolve("fine-" + fineNumber + ".pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                // ===== Header =====
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(margin, y);
                cs.showText("Traffic Department - Municipality");
                cs.endText();

                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Fine number: " + fineNumber + "   Issue date: " + TS_FMT.format(java.time.Instant.now()));
                cs.endText();

                // ===== Vehicle info =====
                y -= 28;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Vehicle data");
                cs.endText();

                y -= 16;
                writeLine(cs, margin, y, "Owner: " + owner);  y -= 14;
                writeLine(cs, margin, y, "Patent: " + v.plate); y -= 14;
                writeLine(cs, margin, y, "Brand/Model/Color: " + brand + " / " + model + " / " + color);

                // ===== Infraction =====
                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Infraction");
                cs.endText();

                y -= 16;
                writeLine(cs, margin, y, "Type: " + type.getCode() + " - " + type.getDescription()); y -= 14;
                writeLine(cs, margin, y, "Device: " + v.deviceId + "   Date/Time: " + TS_FMT.format(v.ts)); y -= 14;
                writeLine(cs, margin, y, "Address: " + address); y -= 14;
                writeLine(cs, margin, y, "Details: " + v.details);

                // ===== Imagen =====
                PDImageXObject img = loadImageFromStatic(doc, photoFileName);
                if (img != null) {
                    float imgW = 300;
                    float ratio = img.getHeight() / (float) img.getWidth();
                    float imgH = imgW * ratio;
                    y -= (imgH + 20);
                    cs.drawImage(img, margin, y, imgW, imgH);
                } else {
                    System.out.println("⚠️ No se pudo cargar la imagen para el PDF.");
                }

                // ===== Amount =====
                y -= 20;
                writeLineBold(cs, margin, y, String.format("Amount: $ %.2f   Points: %d", calc.amount(), calc.points()));

                // ===== Barcode =====
                y -= 28;
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 18);
                cs.newLineAtOffset(margin, y);
                cs.showText(barcode);
                cs.endText();

                // ===== Footer =====
                y -= 24;
                writeLine(cs, margin, y, "Please submit this document within 10 business days.");
            }

            doc.save(out.toFile());
            System.out.println("✅ PDF generado en: " + out.toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }

        return out;
    }

    /**
     * Carga la imagen desde la carpeta "static/images".
     * Ejemplo de uso: loadImageFromStatic(doc, "CameraPhoto.png");
     */
    private static PDImageXObject loadImageFromStatic(PDDocument doc, String fileName) {
        try {
            File f = new File("src/main/resources/static/images/" + fileName);
            if (!f.exists()) {
                System.out.println("⚠️ Imagen no encontrada: " + f.getAbsolutePath());
                return null;
            }
            System.out.println("📁 Cargando imagen desde: " + f.getAbsolutePath());
            return PDImageXObject.createFromFileByContent(f, doc);
        } catch (IOException e) {
            System.out.println("❌ Error al cargar imagen: " + e.getMessage());
            return null;
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
