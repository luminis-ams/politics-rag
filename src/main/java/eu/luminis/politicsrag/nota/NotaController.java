package eu.luminis.politicsrag.nota;

import eu.luminis.politicsrag.model.QuestionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotaController {

    private final NotaQuestionAnswerService questionAnswerService;

    public NotaController(NotaQuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @GetMapping("/nota")
    public String home(QuestionForm questionForm, Model model) {
        return "nota";
    }

    @PostMapping("/nota")
    public String answer(QuestionForm questionForm, Model model) {

        if (questionForm.getMessageText() != null) {
            String answer = questionAnswerService.answerNotaQuestion(questionForm.getMessageText());
            model.addAttribute("answer", answer);
        }

        return "nota";
    }

}
