package eu.luminis.politicsrag.evaluate;

import lombok.Builder;
import lombok.Getter;


@Getter
public class EvaluateResponse {
    private final String evaluation;

    @Builder
    public EvaluateResponse(String evaluation) {
        this.evaluation = evaluation;
    }

}
