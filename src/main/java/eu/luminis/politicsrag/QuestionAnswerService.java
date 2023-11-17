package eu.luminis.politicsrag;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import eu.luminis.politicsrag.custom.MultiRetrievalChain;
import eu.luminis.politicsrag.custom.MultiRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerService {
    private final Logger LOGGER = LoggerFactory.getLogger(QuestionAnswerService.class);

    private final ChatLanguageModel chatLanguageModel;
    private final EmbeddingStoreRetriever retriever;
    private final MultiRetriever multiRetriever;

    private final ConversationalRetrievalChain notaChain;
    private final MultiRetrievalChain partiesChain;

    public QuestionAnswerService(ChatLanguageModel chatLanguageModel, EmbeddingStoreRetriever retriever, MultiRetriever multiRetriever) {
        this.chatLanguageModel = chatLanguageModel;
        this.retriever = retriever;
        this.multiRetriever = multiRetriever;
        this.partiesChain = createMultiRetrievalChain();
        this.notaChain = createRetrievalChain();
    }

    public String answerQuestion(String question) {

        String answer = partiesChain.execute(question);
        LOGGER.info("Answer: {}", answer);

        return answer;
    }

    public String answerNotaQuestion(String question) {

        String answer = notaChain.execute(question);
        LOGGER.info("Answer: {}", answer);

        return answer;
    }

    private ConversationalRetrievalChain createRetrievalChain() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(chatLanguageModel)
                .promptTemplate(createPromptTemplate())
                .retriever(retriever)
                .build();
    }

    private MultiRetrievalChain createMultiRetrievalChain() {
        return MultiRetrievalChain.builder()
                .chatLanguageModel(chatLanguageModel)
                .promptTemplate(createMultiPromptTemplate())
                .retriever(multiRetriever)
                .build();
    }

    private PromptTemplate createMultiPromptTemplate() {
        return PromptTemplate.from("""
                Based on the question: '{{question}}' you get a number of responses from different political parties. Information is between backticks. The format for each party is:
                ...political_party: vvd
                text: this is a answer to the question by the vvd...```{{information}}```
                Answer in the same language as the question. The answer should be in an html list format.Answer:""");
    }

    private PromptTemplate createPromptTemplate() {
        return PromptTemplate.from("""
                Based on the question: '{{question}}' you get a number of excerpts from a document, use them to answer the question that is asked. The excerpts are in the block between backticks.
                ```{{information}}```
                Answer in the same language as the question. Limit to a maximum of 10 sentences.
                Answer:""");
    }

}
