package eu.luminis.politicsrag.custom;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiRetriever implements Retriever<TextSegment> {
    private final String WEAVIATE_API_KEY = System.getenv("WEAVIATE_API_KEY");
    private final String WEAVIATE_HOST = System.getenv("WEAVIATE_HOST");

    private static final int MAX_RESULTS = 1;
    private final EmbeddingModel embeddingModel;
    private final Map<String, WeaviateEmbeddingStore> embeddingStores = new HashMap<>();

    public MultiRetriever(EmbeddingModel embeddingModel, List<String> resourceNames) {
        this.embeddingModel = embeddingModel;

        resourceNames.forEach(resourceName -> {
            WeaviateEmbeddingStore embeddingStore = WeaviateEmbeddingStore.builder()
                    .apiKey(WEAVIATE_API_KEY)
                    .host(WEAVIATE_HOST)
                    .scheme("https")
                    .objectClass(resourceName)
                    .build();
            embeddingStores.put(resourceName, embeddingStore);
        });

    }

    @Override
    public List<TextSegment> findRelevant(String text) {
        List<TextSegment> allTextSegments = new ArrayList<>();

        Embedding embeddedText = embeddingModel.embed(text).content();

        embeddingStores.forEach((key, value) -> {
            List<EmbeddingMatch<TextSegment>> relevant = value.findRelevant(embeddedText, MAX_RESULTS);

            allTextSegments.addAll(relevant.stream()
                    .map(EmbeddingMatch::embedded)
                    .map(ts -> TextSegment.from(ts.text(), Metadata.from(Map.of("resource", key))))
                    .toList());
        });

        return allTextSegments;
    }
}
