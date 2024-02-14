package eu.luminis.politicsrag.ingest;

import eu.luminis.politicsrag.ingest.IngestService;
import eu.luminis.politicsrag.ingest.PdfExtractorService;
import eu.luminis.politicsrag.model.PoliticalParties;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IngestController {

    private final PdfExtractorService pdfExtractorService;
    private final IngestService ingestService;

    public IngestController(PdfExtractorService pdfExtractorService, IngestService ingestService) {
        this.pdfExtractorService = pdfExtractorService;
        this.ingestService = ingestService;
    }

    @GetMapping("/ingest")
    public String ingest() {
        return "ingest";
    }

    @PostMapping("/ingest")
    public String ingestDocuments(RedirectAttributes redirectAttributes) {
        PoliticalParties.parties.forEach((party, file) -> {
            ingestService.ingestDocument(pdfExtractorService.extract(file), party);
        });
        redirectAttributes.addFlashAttribute("message", "Partijen documenten zijn geïmporteerd!");
        return "redirect:/ingest";
    }

    @PostMapping("/ingest-nota")
    public String ingestNota(RedirectAttributes redirectAttributes) {
        ingestService.ingestMiljoenenNota(pdfExtractorService.extract("miljoenennota-2024"));
        redirectAttributes.addFlashAttribute("message", "De miljoenennota is geïmporteerd!");
        return "redirect:/ingest";
    }

}
