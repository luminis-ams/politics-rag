package eu.luminis.politicsrag.custom;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RetrievalOutput {
    private final String question;
    private final List<String> retrievals;
    private final String answer;

    @Builder
    private RetrievalOutput(String question, List<String> retrievals, String answer) {
        this.question = question;
        this.retrievals = retrievals;
        this.answer = answer;
    }

}
