package eu.luminis.politicsrag.ingest;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.PdfDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class PdfExtractorService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PdfExtractorService.class);

    public Document extract(String pdfName) {
        String resourceLocation = "classpath:data/" + pdfName + ".pdf";
        try {
            PdfDocumentParser parser = new PdfDocumentParser();
            File file = ResourceUtils.getFile(resourceLocation);
            return parser.parse(Files.newInputStream(file.toPath()));
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file :{}", resourceLocation);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("Could not read file", e);
            throw new RuntimeException(e);
        }
    }
}
