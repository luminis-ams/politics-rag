package eu.luminis.politicsrag.ingest;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@Service
public class PdfExtractorService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PdfExtractorService.class);

    public Document extract(String pdfName) {
        String resourceLocation = "classpath:data/" + pdfName + ".pdf";
        try {
            File file = ResourceUtils.getFile(resourceLocation);
            Path filePath = file.toPath();
            return FileSystemDocumentLoader.loadDocument(filePath, new ApachePdfBoxDocumentParser());
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file :{}", resourceLocation);
            throw new RuntimeException(e);
        }
    }
}
