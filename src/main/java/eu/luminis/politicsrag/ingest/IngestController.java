package eu.luminis.politicsrag.ingest;

import eu.luminis.politicsrag.ingest.IngestService;
import eu.luminis.politicsrag.ingest.PdfExtractorService;
import eu.luminis.politicsrag.model.PoliticalParties;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IngestController {

    private final PdfExtractorService pdfExtractorService;
    private final IngestService ingestService;

    public IngestController(PdfExtractorService pdfExtractorService, IngestService ingestService) {
        this.pdfExtractorService = pdfExtractorService;
        this.ingestService = ingestService;
    }

    @GetMapping("/ingest")
    public String ingestDocuments() {
        PoliticalParties.parties.forEach((party, file) -> {
            ingestService.ingestDocument(pdfExtractorService.extract(file), party);
        });
        return "ingest";
    }

}
