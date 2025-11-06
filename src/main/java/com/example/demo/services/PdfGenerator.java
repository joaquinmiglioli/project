package com.example.demo.services;


import fines.Fine;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


//crea un PDF a partir de un objeto Fine


public final class PdfGenerator {


    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());


    private PdfGenerator() {}


    public static Path generateFinePDF(Path outDir, Fine f) {       //toma los datos del Fine
        String fineNumber = String.format("%06d", f.getFineId());
        Path out = outDir.resolve("fine-" + fineNumber + ".pdf");


        String owner  = (f.getCar() != null && f.getCar().getOwner()  != null) ? f.getCar().getOwner()  : "";
        String plate  = (f.getCar() != null && f.getCar().getPlate()  != null) ? f.getCar().getPlate()  : "-";
        String brand  = (f.getCar() != null && f.getCar().getBrand()  != null) ? f.getCar().getBrand().getName() : "";
        String model  = (f.getCar() != null && f.getCar().getModel()  != null) ? f.getCar().getModel().getName() : "";
        String color  = (f.getCar() != null && f.getCar().getColour() != null) ? f.getCar().getColour() : "";
        String addr   = (f.getCar() != null && f.getCar().getAddress()!= null) ? f.getCar().getAddress(): "";
        String photo  = f.getPhotoUrl() != null ? f.getPhotoUrl() : "fallback.png";
        String type   = f.getType() != null ? f.getType().name() : "UNKNOWN";
        String barcode= f.getBarcode() != null ? f.getBarcode() : "";


        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);


            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;


                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(margin, y);
                cs.showText("Traffic Department - Municipality");
                cs.endText();


                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Fine number: " + fineNumber + "   Issue date: " + TS_FMT.format(f.getFineDate()));
                cs.endText();


                y -= 28;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Vehicle data");
                cs.endText();


                y -= 16;
                writeLine(cs, margin, y, "Owner: " + owner);  y -= 14;
                writeLine(cs, margin, y, "Patent: " + plate); y -= 14;
                writeLine(cs, margin, y, "Brand/Model/Color: " + brand + " / " + model + " / " + color);


                y -= 24;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Infraction");
                cs.endText();


                y -= 16;
                writeLine(cs, margin, y, "Type: " + type); y -= 14;
                writeLine(cs, margin, y, "Device: " + f.getDeviceId() + "   Date/Time: " + TS_FMT.format(f.getFineDate())); y -= 14;
                writeLine(cs, margin, y, "Address: " + addr);


                PDImageXObject img = loadImageFromStatic(doc, photo);
                if (img != null) {
                    float imgW = 300;
                    float ratio = img.getHeight() / (float) img.getWidth();
                    float imgH = imgW * ratio;
                    y -= (imgH + 20);
                    cs.drawImage(img, margin, y, imgW, imgH);
                } else {
                    y -= 20;
                    writeLine(cs, margin, y, "(No evidence photo available)");
                }


                y -= 20;
                writeLineBold(cs, margin, y,
                        String.format("Amount: $ %.2f   Points: %d", f.getAmount(), f.getScoringPoints()));


                if (!barcode.isBlank()) {
                    y -= 28;
                    cs.beginText();
                    cs.setFont(PDType1Font.COURIER_BOLD, 18);
                    cs.newLineAtOffset(margin, y);
                    cs.showText(barcode);
                    cs.endText();
                }


                y -= 24;
                writeLine(cs, margin, y, "Please submit this document within 10 business days.");
            }


            doc.save(out.toFile());
            System.out.println("✅ PDF generated in: " + out.toAbsolutePath());


        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }


        return out;
    }


    /** Carga la imagen desde la carpeta "static/images/fines". */
    private static PDImageXObject loadImageFromStatic(PDDocument doc, String nameOrPath) {
        try {
            File f = (nameOrPath.contains("/") || nameOrPath.contains("\\"))
                    ? new File(nameOrPath)
                    : new File("src/main/resources/static/images/fines/" + nameOrPath);


            if (!f.exists()) {
                System.out.println("⚠️ Image not found: " + f.getAbsolutePath());
                return null;
            }
            return PDImageXObject.createFromFileByContent(f, doc);
        } catch (IOException e) {
            System.out.println("❌ Error loading image: " + e.getMessage());
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
