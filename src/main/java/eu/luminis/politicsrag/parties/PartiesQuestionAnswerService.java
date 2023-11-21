package eu.luminis.politicsrag.parties;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import eu.luminis.politicsrag.custom.MultiRetrievalChain;
import eu.luminis.politicsrag.custom.MultiRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PartiesQuestionAnswerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartiesQuestionAnswerService.class);

    private final ChatLanguageModel chatLanguageModel;
    private final MultiRetriever multiRetriever;
    private final MultiRetrievalChain partiesChain;

    public PartiesQuestionAnswerService(ChatLanguageModel chatLanguageModel, MultiRetriever multiRetriever) {
        this.chatLanguageModel = chatLanguageModel;
        this.multiRetriever = multiRetriever;
        this.partiesChain = createMultiRetrievalChain();
    }

    public String answerQuestion(String question) {
        LOGGER.info("Question: {}", question);
        String answer = partiesChain.execute(question);
        LOGGER.info("Answer: {}", answer);
        return answer;
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

}
