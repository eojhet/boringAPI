package com.eojhet.boring.pdf;

import com.eojhet.boring.services.BoringObjectDecoder;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoringPDF {
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final BoringObjectDecoder boringData;

    public BoringPDF(String boringJson) {
        this.boringData = new BoringObjectDecoder(boringJson);
    }

    public String[] make() throws IOException {
        String fileName = boringData.getId() + " " + boringData.getLocation();
        String filePath = "output/" + new Date().toInstant().toString() + fileName + " Boring Log.pdf";
//        String filePath = "output/BoringTest.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        document.setMargins(20, 30, 20, 30);

        SiteInfo siteInfo = new SiteInfo();

        document.add(siteInfo.title());
        document.add(siteInfo.info1(boringData));
        document.add(siteInfo.info2(boringData));
        document.add(boringLog());
        document.close();

        return new String[]{filePath, fileName};
    }

    private Table boringLog() {
        PdfFont font;
        PdfFont bold;

        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Table tableBoring = new Table(new float[]{0.85f,1,1,1,5},true);

        // Build log heading
        String[] header = {"Graphical\nLog", "Top Depth\n(FT)", "Thick.\n(FT)", "Bt.Elev.\n(FT)", "Material\nDescription"};
        for (String label : header) {
            tableBoring.addCell(new Cell().add(new Paragraph(label).setFont(bold)).setFontSize(9).setPaddingLeft(4));
        }

        // Creates empty margin between log heading and log
        tableBoring.addCell(new Cell(1,5).add(new Paragraph(" ")).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER));

        ArrayList<Float> depths = boringData.getDepths();
        ArrayList<String> types = boringData.getTypes();
        ArrayList<String> descriptions = boringData.getDescriptions();

        // scale will always fit graphic boring log on single page
        float scale = (float) Math.floor(30f/(depths.get(depths.size() -1)) * 20f);
        if (scale > 30) {
            scale = 30;
        }

        float topDepth = 0f;
        Cell boringCell = new Cell().setFont(font).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setPaddingLeft(6);

        for(int i = 0; i < depths.size(); i++) {

            float depth = depths.get(i);
            float lastDepth = 0;
            if (i > 0) {
                lastDepth = depths.get(i-1);
            }
            float thickness = depth - topDepth;
            if (thickness < 0.5f) {
                thickness = 0.5f;
            }

            // Build image data from png and pattern inside of paragraph to insert into log cell
            String patternLocation = "src/main/resources/patterns/" + types.get(i) + ".png";
            ImageData imageData;
            try {
                imageData = ImageDataFactory.create(patternLocation);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Image pdfImg = new Image(imageData);
            pdfImg.setWidth(10).setHeight(10);
            Paragraph pattern = new Paragraph();

            // calculate how many pattern elements fit into graphic boring log cell paragraph
            int amount = (int) Math.ceil((thickness * scale)/10f * 5f);

            // Place pattern elements into graphic boring log cell paragraph
            for (int j = 0; j < amount; j++) {
                pattern.add(pdfImg);
            }

            tableBoring.addCell(new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingLeft(1).setPaddingRight(0).setPaddingTop(0).setPaddingBottom(0).add(pattern).setHeight(thickness*scale));
            // Top Depth
            tableBoring.addCell(boringCell.clone(false).add(new Paragraph(df.format(lastDepth))));
            // Thickness
            tableBoring.addCell(boringCell.clone(false).add(new Paragraph(df.format(depth - lastDepth))));
            // Bottom Elevation
            tableBoring.addCell(boringCell.clone(false).add(new Paragraph(df.format(depth))));
            // Material Description
            tableBoring.addCell(boringCell.clone(false).add(new Paragraph(descriptions.get(i))));
            topDepth += thickness;
        }

        // Total depth at log bottom
        boringCell.setBorderTop(Border.NO_BORDER);
        tableBoring.addCell(boringCell.clone(false).add(new Paragraph(depths.get(depths.size() -1) + " FT bgs").setFont(font)).setFontSize(9).setPadding(0));
        for (int i = 0; i < 4; i++) {
            tableBoring.addCell(boringCell);
        }

        return tableBoring;
    }

    public static void main(String[] args) throws IOException {
        String boringObj = "{\"id\":\"MW-1\",\"location\":\"69 Freeway Junction\",\"siteName\":\"The Homestead\",\"logBy\":\"Joe G\",\"company\":\"Bay Env\",\"equip\":\"Hand Auger\",\"date\":\"2021-09-13\",\"time\":\"07:50\",\"depths\":[\"0.25\",\"0.5\",\"13.5\",\"30\"],\"types\":[\"topSoil\",\"clay\",\"siltyClay\",\"silt\"],\"descriptions\":[\"Topsoil\",\"Clay\",\"Silty Clay\",\"Silt\"]}";

        new BoringPDF(boringObj).make();
    }

}
