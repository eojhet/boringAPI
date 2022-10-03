package com.eojhet.boring.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class BoringPDF {
    private final String[] info1 = {"Boring ID: \n", "Logged By: \n", "Company: \n"};
    private final String[] info2 = {"Location: \n", "Equipment: \n", "Date: \n", "Time: \n"};
    private final String[] header = {"Graphical\nLog", "Top Depth\n(FT)", "Thick.\n(FT)", "Bt.Elev.\n(FT)", "Material\nDescription"};

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

        Table tableInfo = new Table(new float[]{1,1,1}, true);

        tableInfo.addCell(new Cell().add(new Paragraph(info1[0] + boringData.getId()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo.addCell(new Cell().add(new Paragraph(info1[1] + boringData.getLogBy()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo.addCell(new Cell().add(new Paragraph(info1[2] + boringData.getCompany()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));

        document.add(tableInfo);

        Table tableInfo2 = new Table(new float[]{3,2,1,1}, true);

        tableInfo2.addCell(new Cell().add(new Paragraph(info2[0] + boringData.getLocation()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
        tableInfo2.addCell(new Cell().add(new Paragraph(info2[1] + boringData.getEquipment()).setFont(font)).setFontSize(9).setBorderBottom(Border.NO_BORDER));
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

        Float topDepth = 0f;
        for(int i = 0; i < depths.size(); i++) {
            Float depth = depths.get(i);
            Float thickness = depth - topDepth;
            // Graphical Log
            tableBoring.addCell(new Cell().add(new Paragraph(types.get(i)).setFont(font)).setFontSize(9).setHeight(thickness*35));
            // Top Depth
            tableBoring.addCell(new Cell().add(new Paragraph("\t"+topDepth.toString()).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER));
            // Thickness
            tableBoring.addCell(new Cell().add(new Paragraph("\t"+thickness.toString()).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER));
            // Bottom Elevation
            tableBoring.addCell(new Cell().add(new Paragraph("\t-" + depth).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER));
            // Material Description
            tableBoring.addCell(new Cell().add(new Paragraph(descriptions.get(i)).setFont(font)).setFontSize(9).setBorderTop(new DashedBorder(0.6f)).setBorderLeft(Border.NO_BORDER));
            topDepth += thickness;
        }


        document.add(tableBoring);
        tableBoring.complete();
        document.close();

        return new String[]{filePath, fileName};
    }

}
