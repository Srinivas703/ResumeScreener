package com.srinivas.resumescreener.service;

import com.srinivas.resumescreener.exception.ResumeParsingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeParserService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    public String extractText(MultipartFile file) {
        validateFile(file);

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new ResumeParsingException("Cannot parse a password-protected PDF");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new ResumeParsingException(
                        "No extractable text found in the PDF. It may be a scanned image - OCR is not yet supported");
            }

            return text.trim();

        } catch (IOException e) {
            throw new ResumeParsingException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResumeParsingException("Resume file is empty or missing");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResumeParsingException("Resume file exceeds maximum size of 10MB");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new ResumeParsingException("Only PDF files are supported");
        }
    }
}
