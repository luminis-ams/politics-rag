package eu.luminis.politicsrag.custom;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import eu.luminis.politicsrag.config.keyloader.KeyLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class MultiRetriever implements ContentRetriever {
    private static final int MAX_RESULTS = 1;
    private final EmbeddingModel embeddingModel;
    private final Map<String, WeaviateEmbeddingStore> embeddingStores = new HashMap<>();

    public MultiRetriever(EmbeddingModel embeddingModel, List<String> resourceNames, KeyLoader keyLoader) {
        this.embeddingModel = embeddingModel;

        resourceNames.forEach(resourceName -> {
            WeaviateEmbeddingStore embeddingStore = WeaviateEmbeddingStore.builder()
                    .apiKey(keyLoader.getWeaviateAPIKey())
                    .host(keyLoader.getWeaviateURL())
                    .scheme("https")
                    .objectClass(resourceName)
                    .build();
            embeddingStores.put(resourceName, embeddingStore);
        });

    }


    public List<Content> findRelevant(Query query, List<String> parties) {
        List<Content> allTextSegments = new ArrayList<>();

        embeddingStores.forEach((key, value) -> {
            if (parties.contains(key)) {
                allTextSegments.addAll(this.retrieve(query));
            }
        });

        return allTextSegments;
    }

    @Override
    public List<Content> retrieve(Query query) {
        List<Content> contents = new ArrayList<>();

        Embedding embeddedText = embeddingModel.embed(query.text()).content();

        embeddingStores.forEach((key, value) -> {
            List<EmbeddingMatch<TextSegment>> relevant = value.findRelevant(embeddedText, MAX_RESULTS);

            contents.addAll(relevant.stream()
                    .map(EmbeddingMatch::embedded)
                    .map(Content::from)
                    .toList());
        });

        return contents;
    }
}
