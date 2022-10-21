package com.eojhet.boring.controller;

import com.eojhet.boring.cleanup.CleanOutput;
import com.eojhet.boring.pdf.ConstructionPDF;
import com.eojhet.boring.services.ByteStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class ConstructionController {
//    @CrossOrigin(origins = "https://boring.eojhet.com")
    @PostMapping(value = "/construction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> createPDF(@RequestBody String jsonObject) {

        ConstructionPDF constructionPDF = new ConstructionPDF(jsonObject);
        String[] pdfPath;
        pdfPath = constructionPDF.make();

        File path = new File(pdfPath[0]);
        String pdfName = pdfPath[1];

        byte[] contents;
        try {
            contents = ByteStream.fileStream(path);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(pdfPath[1], pdfPath[0]);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        CleanOutput.cleanIfOver(32);

        return new ResponseEntity<>(contents, headers,  HttpStatus.CREATED);
    }


}
