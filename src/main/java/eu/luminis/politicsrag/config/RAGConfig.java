package eu.luminis.politicsrag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import eu.luminis.politicsrag.model.PoliticalParties;
import eu.luminis.politicsrag.custom.MultiRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAGConfig {

    private final String OPEN_AI_API_KEY = System.getenv("OPEN_AI_API_KEY");
    private final String WEAVIATE_API_KEY = System.getenv("WEAVIATE_API_KEY");
    private final String WEAVIATE_HOST = System.getenv("WEAVIATE_HOST");

    @Bean
    public EmbeddingModel embeddingModel() {

        return OpenAiEmbeddingModel.builder()
                .apiKey(OPEN_AI_API_KEY)
                .modelName(OpenAiModelName.TEXT_EMBEDDING_ADA_002)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {

        return WeaviateEmbeddingStore.builder()
                .apiKey(WEAVIATE_API_KEY)
                .host(WEAVIATE_HOST)
                .scheme("https")
                .objectClass("Miljoenennota")
                .build();
    }

    @Bean
    public EmbeddingStoreRetriever embeddingStoreRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreRetriever.from(embeddingStore, embeddingModel);
    }

    @Bean
    public MultiRetriever multiRetriever(EmbeddingModel embeddingModel) {
        return new MultiRetriever(embeddingModel, PoliticalParties.parties.keySet().stream().toList());
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(OPEN_AI_API_KEY)
                .modelName("gpt-4-1106-preview")
                .build();
    }
}
