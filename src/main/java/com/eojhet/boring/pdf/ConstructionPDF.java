package com.eojhet.boring.pdf;

import com.eojhet.boring.services.WellConstructionObjectDecoder;
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
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ConstructionPDF {
    private final WellConstructionObjectDecoder boringData;

    public ConstructionPDF(String boringJson) {
        this.boringData = new WellConstructionObjectDecoder(boringJson);
    }

    public String[] make() throws IOException {
        String fileName = boringData.getId() + " " + boringData.getLocation() + " Construction Log.pdf";
//        String filePath = "output/" + new Date().toInstant().toString() + fileName;
        String filePath = "output/Construction.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);

        document.setMargins(20, 30, 20, 30);

        SiteInfo siteInfo = new SiteInfo();

        document.add(siteInfo.title());
        document.add(siteInfo.info1(boringData));
        document.add(siteInfo.info2(boringData));
        document.add(logHeaders());

        document.add(graphicLog());
        document.close();

        return new String[]{filePath, fileName};
    }

    private Table logHeaders () {
        PdfFont bold;

        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] header = {"Description of String", "String\nType", "Depth:\nFrom - To (ft)", "Graphical Log", "Depth:\nFrom - To (ft)", "Annular Fill\nMaterial", "Description of Material"};

        Table tableHeaders = new Table(new float[]{1.5f,0.75f,0.75f,0.55f,0.75f,0.75f,1.5f},true);

        for (int i = 0; i < header.length; i++) {
            tableHeaders.addCell(new Cell().add(new Paragraph(header[i])).setFont(bold).setFontSize(9));
        }

        return tableHeaders;
    }
    private Table graphicLog() {
        PdfFont font;

        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DecimalFormat df = new DecimalFormat("0.00");

        HashMap<String,String> fillMaterial = new HashMap<>(){{
            put("backFill", "Back Fill");
            put("seal", "Seal");
            put("filterPack", "Filter pack");
        }};

        ArrayList<Float> depths = boringData.getMaterialDepths();
        ArrayList<String> types = boringData.getMaterialTypes();
        ArrayList<String> descriptions = boringData.getMaterialDescriptions();
        float standupHeightCorrected;
        if (boringData.getStandupHeight() < 0.4f) {
            standupHeightCorrected = 0.4f;
        } else {
            standupHeightCorrected = boringData.getStandupHeight();
        }

        float scale = (float) Math.floor(31/(depths.get(depths.size() -1) + boringData.getStandupHeight()) * 20);
        if (scale > 34) {
            scale = 34;
        }

        // outerTable will hold new tables as cells to appear as one table
        Table outerTable = new Table(new float[]{3,0.15f,0.25f,0.15f,3},true);
        outerTable.addCell(new Cell(1,5).add(new Paragraph(" ")).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));

        // Table for Casing, Screen, and Standup Data
        Table wellTable = new Table(new float[]{1.5f,0.75f,0.75f},true);

        // Pre-optioned cells for reuse
        Cell dataCell = new Cell().setFont(font).setFontSize(9).setPaddingTop(0).setPaddingBottom(0).setBorderTop(new DashedBorder(0.6f)).setBorderRight(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
        Cell graphicCell = new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingBottom(0).setPaddingTop(0).setPaddingRight(0).setPaddingLeft(0.5f);
        Cell stringGraphicCell = new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingBottom(0).setPaddingTop(0).setPaddingRight(0).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER);

        // STANDUP data if it exists
        if (boringData.getStandupHeight() > 0) {
            wellTable.addCell(dataCell.clone(false).add(new Paragraph(boringData.getCasingDescription())).setHeight(standupHeightCorrected*scale));
            wellTable.addCell(dataCell.clone(false).add(new Paragraph("Standup")));
            wellTable.addCell(dataCell.clone(false).add(new Paragraph(df.format(boringData.getStandupHeight()) + " - 0.00")));
        }

        // CASING data
        wellTable.addCell(dataCell.clone(false).add(new Paragraph(boringData.getCasingDescription())).setHeight(boringData.getCasingDepth()*scale));
        wellTable.addCell(dataCell.clone(false).add(new Paragraph("Riser")));
        wellTable.addCell(dataCell.clone(false).add(new Paragraph("0.00 - " + df.format(boringData.getCasingDepth()))));

        // SCREEN data
        wellTable.addCell(dataCell.clone(false).add(new Paragraph(boringData.getScreenDescription())));
        wellTable.addCell(dataCell.clone(false).add(new Paragraph("Screen")));
        wellTable.addCell(dataCell.clone(false).add(new Paragraph(df.format(boringData.getCasingDepth()) + " - " + df.format(boringData.getScreenDepth()))));

        outerTable.addCell(new Cell().add(wellTable).setPadding(0).setBorder(Border.NO_BORDER));

        // Tables for Material data and Material graphic log
        Table materialTable = new Table(new float[]{0.75f,0.75f,1.5f},true);
        Table materialGraphicTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        float totalDepth = 0f;

        // Add padding to top of Material data and Material graphic log if STANDUP exists
        if (boringData.getStandupHeight() > 0) {
            materialGraphicTable.addCell(new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingTop(0).setPaddingBottom(0.5f).setBorder(Border.NO_BORDER).setHeight(standupHeightCorrected*scale));
            materialTable.addCell(new Cell(1,3).setBorder(Border.NO_BORDER).setPadding(0).setHeight(standupHeightCorrected*scale));
        }

        // Loop to build Material data and Material graphic log
        for (int i = 0; i < depths.size(); i++) {

            float depth = depths.get(i);
            float lastDepth = 0;
            if (i > 0) {
                lastDepth = depths.get(i-1);
            }
            float thickness = depth - totalDepth;
            if (thickness < 0.4f) {
                thickness = 0.4f;
            }

            String patternLocation = "src/main/resources/patterns/" + types.get(i) + ".png";
            Paragraph pattern = patternBuilder(scale, thickness, patternLocation, true);

            materialTable.addCell(dataCell.clone(false).add(new Paragraph(df.format(lastDepth) + " - " + df.format(depth))).setHeight(thickness*scale));
            materialTable.addCell(dataCell.clone(false).add(new Paragraph(fillMaterial.get(types.get(i)))));
            materialTable.addCell(dataCell.clone(false).add(new Paragraph(descriptions.get(i))));

            materialGraphicTable.addCell(graphicCell.clone(false).add(pattern).setHeight(thickness*scale));
            totalDepth += thickness;
        }

        // Center Graphical String Log
        Table stringTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

        // Build STANDUP graphic if one exists
        if (boringData.getStandupHeight() > 0) {
            stringTable.addCell(new Cell().setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0).setHeight(standupHeightCorrected*scale));
        }

        // Build CASING graphic
        stringTable.addCell(stringGraphicCell.clone(false).setHeight(boringData.getCasingDepth()*scale));

        // Build SCREEN graphic with pattern
        String screenPatternLocation = "src/main/resources/patterns/screen.png";
        Paragraph screenPattern = patternBuilder(scale, boringData.getScreenDepth() - boringData.getCasingDepth(), screenPatternLocation, false);
        stringTable.addCell(stringGraphicCell.clone(false).add(screenPattern).setHeight((boringData.getScreenDepth() - boringData.getCasingDepth())*scale));

        // Fill in bottom area below string if Material runs deeper than string
        if (depths.get(depths.size()-1) > boringData.getScreenDepth()) {
            float thickness = depths.get(depths.size()-1) - boringData.getScreenDepth();
            String patternLocation = "src/main/resources/patterns/" + types.get(depths.size()-1) + ".png";
            Paragraph pattern = patternBuilder(scale, thickness, patternLocation, false);
            stringTable.addCell(stringGraphicCell.clone(false).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(pattern).setHeight(thickness*scale));
        }

        // Add Material graphic log to outerTable
        outerTable.addCell(new Cell().add(materialGraphicTable).setPaddingRight(0).setPaddingTop(0).setPaddingBottom(0).setBorder(Border.NO_BORDER));

        // Add String graphic log to outerTable
        outerTable.addCell(new Cell().add(stringTable).setPadding(0).setBorder(Border.NO_BORDER));

        // Add Material graphic log to outerTable
        outerTable.addCell(new Cell().add(materialGraphicTable).setPaddingLeft(0).setPaddingTop(0).setPaddingBottom(0).setBorder(Border.NO_BORDER));

        // Add Material data log to outerTable
        outerTable.addCell(new Cell().add(materialTable).setPadding(0).setBorder(Border.NO_BORDER));

        // Add Total Depth to bottom of graphical log
        outerTable.addCell(new Cell(1,1).setPadding(0).setBorder(Border.NO_BORDER));
        outerTable.addCell(new Cell(1,3).add(new Paragraph((df.format(depths.get(depths.size() - 1)) + " ft T.D."))).setFont(font).setFontSize(8.25f).setBorder(Border.NO_BORDER).setPadding(0));
        outerTable.addCell(new Cell(1,1).setPadding(0).setBorder(Border.NO_BORDER));

        return outerTable;
    }

    private Paragraph patternBuilder(float scale, float thickness, String patternLocation, boolean singleWide) {
        // Build image data from png and pattern inside of paragraph to insert into log cell
        ImageData imageData;
        try {
            imageData = ImageDataFactory.create(patternLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Image pdfImg = new Image(imageData);
        pdfImg.setWidth(8).setHeight(8);
        Paragraph pattern = new Paragraph();

        // calculate how many pattern elements fit into graphic boring log cell paragraph
        double volume = Math.ceil((thickness * scale) / 8);
        int amount = singleWide ? (int) volume - 1: (int) volume * 2 - 2;

        // Place pattern elements into graphic boring log cell paragraph
        for (int j = 0; j < amount; j++) {
            pattern.add(pdfImg);
        }

        return pattern;
    }

    public static void main(String[] args) throws IOException {
        // TEST

        String boringObj = "{\"id\":\"MW-1\",\"location\":\"123 Franklin St, Chesapeake, VA\",\"siteName\":\"Albert Property\",\"logBy\":\"Dog Bounty\",\"company\":\"Steve's Holes Inc.\",\"equip\":\"Hand Auger\",\"date\":\"2021-09-13\",\"time\":\"07:50\",\"depths\":[\"1\",\"4\",\"8\",\"16\"],\"types\":[\"topSoil\",\"clay\",\"claySand\",\"sand\"],\"descriptions\":[\"Topsoil\",\"Hard red clay\",\"Loose beige clay sand\",\"Dark petroleum contaminated sand\"],\"standupHeight\":\"0.25\",\"casingDepth\":\"2\",\"casingDesc\":\"Two-inch solid PVC\",\"screenDepth\":\"16\",\"screenDesc\":\"Two-inch slotted PVC\",\"materialDepths\":[\"0.25\",\"0.5\",\"16\"],\"materialTypes\":[\"backFill\",\"seal\",\"filterPack\"],\"materialDescriptions\":[\"Topsoil\",\"Medium Bentonite Chips\",\"No. 2 Gravel Pack\"]}";

        new ConstructionPDF(boringObj).make();
    }

}
