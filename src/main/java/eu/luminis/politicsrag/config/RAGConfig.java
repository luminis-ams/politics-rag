package eu.luminis.politicsrag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import eu.luminis.politicsrag.config.keyloader.KeyLoader;
import eu.luminis.politicsrag.model.PoliticalParties;
import eu.luminis.politicsrag.custom.MultiRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAGConfig {

    @Bean
    public KeyLoader keyLoader() {
        return new KeyLoader();
    }

    @Bean
    public EmbeddingModel embeddingModel(KeyLoader keyLoader) {

        return OpenAiEmbeddingModel.builder()
                .apiKey(keyLoader.getOpenAIKey())
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(KeyLoader keyLoader) {

        return WeaviateEmbeddingStore.builder()
                .apiKey(keyLoader.getWeaviateAPIKey())
                .host(keyLoader.getWeaviateURL())
                .scheme("https")
                .objectClass("Miljoenennota")
                .build();
    }

    @Bean
    public EmbeddingStoreRetriever embeddingStoreRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreRetriever.from(embeddingStore, embeddingModel);
    }

    @Bean
    public MultiRetriever multiRetriever(EmbeddingModel embeddingModel, KeyLoader keyLoader) {
        return new MultiRetriever(embeddingModel,
                PoliticalParties.parties.keySet().stream().toList(), keyLoader);
    }

    @Bean
    public ChatLanguageModel chatLanguageModel(KeyLoader keyLoader) {
        return OpenAiChatModel.builder()
                .apiKey(keyLoader.getOpenAIKey())
                .modelName(OpenAiChatModelName.GPT_4)
                .build();
    }
}
