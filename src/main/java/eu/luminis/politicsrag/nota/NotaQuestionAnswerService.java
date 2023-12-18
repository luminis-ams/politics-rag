package eu.luminis.politicsrag.nota;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import eu.luminis.politicsrag.custom.CustomConversationalRetrievalChain;
import eu.luminis.politicsrag.custom.RetrievalOutput;
import eu.luminis.politicsrag.evaluate.EvaluateResponse;
import eu.luminis.politicsrag.evaluate.EvaluatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotaQuestionAnswerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotaQuestionAnswerService.class);

    private final ChatLanguageModel chatLanguageModel;
    private final EmbeddingStoreRetriever retriever;
    private final CustomConversationalRetrievalChain notaChain;
    private final EvaluatorService evaluatorService;

    public NotaQuestionAnswerService(ChatLanguageModel chatLanguageModel,
                                     EmbeddingStoreRetriever retriever,
                                     EvaluatorService evaluatorService) {
        this.chatLanguageModel = chatLanguageModel;
        this.retriever = retriever;
        this.evaluatorService = evaluatorService;
        this.notaChain = createRetrievalChain();
    }

    public String answerNotaQuestion(String question) {
        LOGGER.info("Question: {}", question);
        RetrievalOutput answer = notaChain.execute(question);
        LOGGER.info("Answer: {}", answer.getAnswer());

        return answer.getAnswer();
    }

    public EvaluateResponse evaluateNotaQuestion(String question, String expectedAnswer) {
        LOGGER.info("Question: {}", question);
        LOGGER.info("Expected answer: {}", expectedAnswer);
        RetrievalOutput answer = notaChain.execute(question);
        LOGGER.info("Answer: {}", answer.getAnswer());

        return evaluatorService.evaluate(expectedAnswer, answer);
    }

    private CustomConversationalRetrievalChain createRetrievalChain() {
        return CustomConversationalRetrievalChain.builder()
                .chatLanguageModel(chatLanguageModel)
                .promptTemplate(createPromptTemplate())
                .retriever(retriever)
                .build();
    }

    private PromptTemplate createPromptTemplate() {
        return PromptTemplate.from("""
                Based on the question: '{{question}}' you get a number of excerpts from a document, use them to answer the question that is asked. The excerpts are in the block between triple backticks.
                Always answer in the same language as the question. Limit to a maximum of 10 sentences.
                ```{{information}}```
                Answer:""");
    }

}
