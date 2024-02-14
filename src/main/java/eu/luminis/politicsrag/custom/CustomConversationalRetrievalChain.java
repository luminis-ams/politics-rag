package eu.luminis.politicsrag.custom;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.retriever.Retriever;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.util.stream.Collectors.joining;

public class CustomConversationalRetrievalChain implements Chain<String, RetrievalOutput> {
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            "Answer the following question to the best of your ability: {{question}}\n" +
                    "\n" +
                    "Base your answer on the following information:\n" +
                    "{{information}}");
    private final ChatLanguageModel chatLanguageModel;
    private final ChatMemory chatMemory;
    private final PromptTemplate promptTemplate;
    private final ContentRetriever retriever;

    @Builder
    public CustomConversationalRetrievalChain(ChatLanguageModel chatLanguageModel,
                                              ChatMemory chatMemory,
                                              PromptTemplate promptTemplate,
                                              ContentRetriever retriever) {
        this.chatLanguageModel = ensureNotNull(chatLanguageModel, "chatLanguageModel");
        this.chatMemory = chatMemory == null ? MessageWindowChatMemory.withMaxMessages(10) : chatMemory;
        this.promptTemplate = promptTemplate == null ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
        this.retriever = ensureNotNull(retriever, "retriever");
    }

    @Override
    public RetrievalOutput execute(String question) {
        RetrievalOutput.RetrievalOutputBuilder builder = RetrievalOutput.builder();
        question = ensureNotBlank(question, "question");
        builder.question(question);

        List<Content> relevantSegments = retriever.retrieve(new Query(question));
        builder.retrievals(relevantSegments.stream()
                .map(Content::textSegment)
                .map(TextSegment::text).toList());

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", format(relevantSegments));

        UserMessage userMessage = promptTemplate.apply(variables).toUserMessage();

        chatMemory.add(userMessage);

        AiMessage aiMessage = chatLanguageModel.generate(chatMemory.messages()).content();

        chatMemory.add(aiMessage);

        String response = aiMessage.text();
        builder.answer(response);

        return builder.build();
    }

    private static String format(List<Content> relevantSegments) {
        return relevantSegments.stream()
                .map(Content::textSegment)
                .map(TextSegment::text)
                .map(segment -> "..." + segment + "...")
                .collect(joining("\n\n"));
    }

}
