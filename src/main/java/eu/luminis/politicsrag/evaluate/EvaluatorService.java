package eu.luminis.politicsrag.evaluate;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import eu.luminis.politicsrag.custom.RetrievalOutput;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EvaluatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorService.class);
    private final ChatLanguageModel chatLanguageModel;
    private static final PromptTemplate SYSTEM_PROMPT = PromptTemplate.from(
            """
                            Evaluate the following questions and pieces of context.\s
                                        
                            Question: Was ketchup originally a type of medicine?
                            Expected output: Yes, in the 1830's ketchup was sold as a medicine.
                            Actual output: Ketchup was sold in the 1830s as medicine. In 1834, it was sold as a cure for an upset stomach by an Ohio physician named John Cook. It wasn't popularized as a condiment until the late 19th century!
                            Evaluation: {
                                "relevant_context": true,
                                "all_context_present": true
                            }
                                        
                            Question: Where did the shortest war in history happen?
                            Expected output: The war took place in Zanzibar.
                            Actual output: The shortest war in history lasted 38 minutes! It was between Britain and Zanzibar and is known as the Anglo-Zanzibar War. This war occurred on August 27, 1896. It was over the ascension of the next Sultan in Zanzibar and resulted in a British victory.
                            Evaluation: {
                                "relevant_context": true,
                                "all_context_present": false
                            }
                                        
                            Question: What animals did Roman's have as pets?
                            Expected output: Ferrets, dogs and monkeys were the most popular pets in the Roman Empire.
                            Actual output: Roman's didn't have cats as pets.
                            Evaluation: {
                                "relevant_context": false,
                                "all_context_present": false
                            }
                    """
    );

    private static final PromptTemplate USER_PROMPT = PromptTemplate.from(
            """
                            Question: {{question}}
                            Expected output: {{expected_output}}
                            Actual output: {{actual_output}}
                            Evaluation:
                    """
    );

    public EvaluatorService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    public EvaluateResponse evaluate(String expectedAnswer, RetrievalOutput retrievalOutput) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", retrievalOutput.getQuestion());
        variables.put("expected_output", expectedAnswer);
        variables.put("actual_output", retrievalOutput.getAnswer());

        UserMessage userMessage = USER_PROMPT.apply(variables).toUserMessage();
        SystemMessage systemMessage = SYSTEM_PROMPT.apply(Map.of()).toSystemMessage();

        AiMessage aiMessage = chatLanguageModel.generate(systemMessage, userMessage).content();

        LOGGER.info("Evaluation: {}", aiMessage.text());

        return EvaluateResponse.builder()
                .evaluation(aiMessage.text())
                .build();
    }
}
