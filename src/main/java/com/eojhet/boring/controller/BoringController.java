package com.eojhet.boring.controller;

import com.eojhet.boring.cleanup.CleanOutput;
import com.eojhet.boring.pdf.BoringPDF;
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
public class BoringController {
    @CrossOrigin(origins = "https://boring.eojhet.com")
    @PostMapping(value = "/boring",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> createPDF(@RequestBody String jsonObject) {

        BoringPDF boringPDF = new BoringPDF(jsonObject);
        String[] pdfPath;
        try {
            pdfPath = boringPDF.make();
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        File path = new File(pdfPath[0]);
        String pdfName = pdfPath[1] + ".pdf";

        byte[] contents;
        try {
            contents = ByteStream.fileStream(path);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(pdfName, pdfName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        CleanOutput.cleanIfOver(10);

      return new ResponseEntity<>(contents, headers,  HttpStatus.CREATED);
    }


}
