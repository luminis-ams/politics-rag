package eu.luminis.politicsrag.parties;

import eu.luminis.politicsrag.model.PoliticalParties;
import eu.luminis.politicsrag.nota.NotaQuestionAnswerService;
import eu.luminis.politicsrag.model.QuestionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PartiesController {

    private final PartiesQuestionAnswerService questionAnswerService;

    public PartiesController(PartiesQuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @GetMapping("/parties")
    public String home(QuestionForm questionForm, Model model) {
        model.addAttribute("parties", String.join(", ", PoliticalParties.parties.keySet()));
        return "parties";
    }

    @PostMapping("/parties")
    public String answer(QuestionForm questionForm, Model model) {
        model.addAttribute("parties", String.join(", ", PoliticalParties.parties.keySet()));

        if (questionForm.getMessageText() != null) {
            String answer = questionAnswerService.answerQuestion(questionForm.getMessageText());
            model.addAttribute("answer", answer);
        }

        return "parties";
    }

}
