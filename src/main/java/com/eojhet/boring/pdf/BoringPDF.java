package com.eojhet.boring.pdf;

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
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoringPDF {
    private final String[] info1 = {"Boring ID: \n", "Logged By: \n", "Company: \n", "Equipment: \n"};
    private final String[] info2 = {"Location: \n", "Site Name: \n", "Date: \n", "Time: \n"};
    private final String[] header = {"Graphical\nLog", "Top Depth\n(FT)", "Thick.\n(FT)", "Bt.Elev.\n(FT)", "Material\nDescription"};
    private final DecimalFormat df = new DecimalFormat("0.00");
    private BoringObjectDecoder boringData;

    public BoringPDF(String boringJson) {
        this.boringData = new BoringObjectDecoder(boringJson);
    }

    public String[] make() throws IOException {
        String fileName = boringData.getId() + " " + boringData.getLocation();
        String filePath = "output/" + new Date().toInstant().toString() + fileName + ".pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);

        document.setMargins(20, 30, 20, 30);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        Table title = new Table(new float[]{1.5f,2,1.1f},true);
        Cell titleCell = new Cell().setHorizontalAlignment(HorizontalAlignment.RIGHT);
        titleCell.add(new Paragraph("BORING LOG by The Boring App").setFont(bold).setFontSize(10));
        titleCell.setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER);

        title.addCell(new Cell().add(new Paragraph("")).setBorderRight(Border.NO_BORDER));
        title.addCell(titleCell);
        title.addCell(new Cell().add(new Paragraph("")).setBorderLeft(Border.NO_BORDER));

        document.add(title);

        Table tableInfo = new Table(new float[]{1,1,1,1}, true);

        tableInfo.addCell(new Cell().add(new Paragraph(info1[0] + boringData.getId()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo.addCell(new Cell().add(new Paragraph(info1[1] + boringData.getLogBy()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo.addCell(new Cell().add(new Paragraph(info1[2] + boringData.getCompany()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo.addCell(new Cell().add(new Paragraph(info1[3] + boringData.getEquipment()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));

        document.add(tableInfo);

        Table tableInfo2 = new Table(new float[]{3,2,1,1}, true);

        tableInfo2.addCell(new Cell().add(new Paragraph(info2[0] + boringData.getLocation()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo2.addCell(new Cell().add(new Paragraph(info2[1] + boringData.getSiteName()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo2.addCell(new Cell().add(new Paragraph(info2[2] + boringData.getDate()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo2.addCell(new Cell().add(new Paragraph(info2[3] + boringData.getTime()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));

        document.add(tableInfo2);

        Table tableBoring = new Table(new float[]{1,1,1,1,5},true);

        for (String label : header) {
            tableBoring.addCell(new Cell().add(new Paragraph(label).setFont(bold)).setFontSize(9));
        }

        tableBoring.addCell(new Cell(1,5).add(new Paragraph(" ")).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER));

        ArrayList<Float> depths = boringData.getDepths();
        ArrayList<String> types = boringData.getTypes();
        ArrayList<String> descriptions = boringData.getDescriptions();

        int scale = (int) Math.floor(30f/(depths.get(depths.size() -1)) * 20f);
        if (scale > 30) {
            scale = 30;
        }

        float topDepth = 0f;

        for(int i = 0; i < depths.size(); i++) {
            float depth = depths.get(i);
            float thickness = depth - topDepth;

            String pattern = "src/main/resources/patterns/" + types.get(i) + ".png";

            ImageData imageData = ImageDataFactory.create(pattern);
            Image pdfImg = new Image(imageData);
            pdfImg.setWidth(10).setHeight(10);

            // Graphical Log
//            tableBoring.addCell(new Cell().add(new Paragraph(types.get(i)).setFont(font)).setFontSize(9).setHeight(thickness*35));

            Paragraph tester = new Paragraph();
            int amount = (int) Math.ceil((thickness * Float.valueOf(scale))/10f * 6f);
            for (int j = 0; j < amount; j++) {
                tester.add(pdfImg);
            }
            tableBoring.addCell(new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0).add(tester).setHeight(thickness*scale));
            // Top Depth
            tableBoring.addCell(new Cell().add(new Paragraph("\t"+df.format(topDepth)).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            // Thickness
            tableBoring.addCell(new Cell().add(new Paragraph("\t"+df.format(thickness)).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            // Bottom Elevation
            tableBoring.addCell(new Cell().add(new Paragraph("\t-" + depth).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            // Material Description
            tableBoring.addCell(new Cell().add(new Paragraph(descriptions.get(i)).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            topDepth += thickness;
        }
        tableBoring.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(depths.get(depths.size() -1) + " FT bgs").setFont(font)).setFontSize(9));
        for (int i = 0; i < 4; i++) {
            tableBoring.addCell(new Cell().setBorder(Border.NO_BORDER));
        }
        document.add(tableBoring);
        tableBoring.complete();
//        document.add(new Table(1).addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(depths.get(depths.size() -1) + " ft"))));
        document.close();

        return new String[]{filePath, fileName};
    }

    public static void main(String[] args) throws IOException {
        String boringObj = "{\"id\":\"MW-1\",\"location\":\"69 Freeway Junction\",\"siteName\":\"The Homestead\",\"logBy\":\"Joe G\",\"company\":\"Bay Env\",\"equip\":\"Hand Auger\",\"date\":\"2021-09-13\",\"time\":\"07:50\",\"depths\":[\"2\",\"5.5\",\"13.5\",\"30\"],\"types\":[\"topSoil\",\"clay\",\"siltyClay\",\"silt\"],\"descriptions\":[\"Topsoil\",\"Clay\",\"Silty Clay\",\"Silt\"]}";

        new BoringPDF(boringObj).make();
    }

}
