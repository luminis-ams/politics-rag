package eu.luminis.politicsrag.partyagent;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import eu.luminis.politicsrag.custom.MultiRetriever;
import eu.luminis.politicsrag.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PartyAgentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyAgentService.class);
    private final PartyAgent partyAgent;
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public PartyAgentService(ChatLanguageModel chatLanguageModel, MultiRetriever multiRetriever) {
        partyAgent = AiServices.builder(PartyAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(new PartyAgentTools(multiRetriever))
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }

    public void answerQuestion(String messageText) {
        LOGGER.info("User Message: {}", messageText);
        chatMessages.add(new ChatMessage("You", messageText));

        String agentMessage = partyAgent.chat(messageText);
        LOGGER.info("Agent Message: {}", agentMessage);
        chatMessages.add(new ChatMessage("Agent", agentMessage));
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }
}
