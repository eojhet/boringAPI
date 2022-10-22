package com.eojhet.boring.pdf;

import com.eojhet.boring.services.ObjectDecoder;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.IOException;

public class SiteInfo {
    private final PdfFont font;
    private final PdfFont bold;

    {
        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Table title() {
        Table title = new Table(new float[]{1.5f,2,1.1f},true);

        Cell titleCell = new Cell().setHorizontalAlignment(HorizontalAlignment.RIGHT).setFontSize(10);
        titleCell.setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER);
        titleCell.add(new Paragraph("BORING LOG by The Boring App").setFont(bold));

        title.addCell(new Cell().add(new Paragraph("")).setBorderRight(Border.NO_BORDER));
        title.addCell(titleCell);
        title.addCell(new Cell().add(new Paragraph("")).setBorderLeft(Border.NO_BORDER));

        return(title);
    }

    public Table info1(ObjectDecoder boringData) {
        String[] info1 = {"Boring ID: \n", "Logged By: \n", "Company: \n", "Equipment: \n"};

        Table tableInfo1 = new Table(new float[]{1,1,1,1}, true);

        Cell infoCell = new Cell().setFont(font).setFontSize(9).setBorderBottom(Border.NO_BORDER);

        tableInfo1.addCell(infoCell.clone(false).add(new Paragraph(info1[0] + boringData.getId())));
        tableInfo1.addCell(infoCell.clone(false).add(new Paragraph(info1[1] + boringData.getLogBy())));
        tableInfo1.addCell(infoCell.clone(false).add(new Paragraph(info1[2] + boringData.getCompany())));
        tableInfo1.addCell(infoCell.clone(false).add(new Paragraph(info1[3] + boringData.getEquipment())));

        return tableInfo1;
    }

    public Table info2(ObjectDecoder boringData) {
        String[] info2 = {"Location: \n", "Site Name: \n", "Date: \n", "Time: \n"};

        Table tableInfo2 = new Table(new float[]{3,2,1,1}, true);

        Cell infoCell = new Cell().setFont(font).setFontSize(9).setBorderBottom(Border.NO_BORDER);

        tableInfo2.addCell(infoCell.clone(false).add(new Paragraph(info2[0] + boringData.getLocation())));
        tableInfo2.addCell(infoCell.clone(false).add(new Paragraph(info2[1] + boringData.getSiteName())));
        tableInfo2.addCell(infoCell.clone(false).add(new Paragraph(info2[2] + boringData.getDate())));
        tableInfo2.addCell(infoCell.clone(false).add(new Paragraph(info2[3] + boringData.getTime())));

        return tableInfo2;
    }
}
