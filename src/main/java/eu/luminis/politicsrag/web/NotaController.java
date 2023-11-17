package eu.luminis.politicsrag.web;

import eu.luminis.politicsrag.QuestionAnswerService;
import eu.luminis.politicsrag.model.QuestionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotaController {
    private final QuestionAnswerService questionAnswerService;

    public NotaController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @GetMapping("/nota")
    public String home(QuestionForm questionForm, Model model) {
        return "nota";
    }

    @PostMapping("/nota")
    public String asnwer(QuestionForm questionForm, Model model) {

        if (questionForm.getMessageText() != null) {
            String answer = questionAnswerService.answerNotaQuestion(questionForm.getMessageText());
            model.addAttribute("answer", answer);
        }

        return "nota";
    }

}
