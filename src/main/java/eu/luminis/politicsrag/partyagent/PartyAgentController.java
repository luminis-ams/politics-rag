package eu.luminis.politicsrag.partyagent;

import eu.luminis.politicsrag.model.PoliticalParties;
import eu.luminis.politicsrag.model.QuestionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PartyAgentController {
    private final PartyAgentService partyAgentService;

    public PartyAgentController(PartyAgentService partyAgentService) {
        this.partyAgentService = partyAgentService;
    }

    @GetMapping("/agent")
    public String getPage(QuestionForm questionForm, Model model) {
        model.addAttribute("parties", String.join(", ", PoliticalParties.parties.keySet()));
        return "agent";
    }

    @PostMapping("/agent")
    public String chat(QuestionForm questionForm, Model model) {
        model.addAttribute("parties", String.join(", ", PoliticalParties.parties.keySet()));

        if (questionForm.getMessageText() != null) {
            partyAgentService.answerQuestion(questionForm.getMessageText());
            model.addAttribute("chatMessages", partyAgentService.getChatMessages());
        }

        return "agent";
    }
}
