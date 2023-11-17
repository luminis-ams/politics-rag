package eu.luminis.politicsrag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import org.springframework.stereotype.Service;

@Service
public class IngestService {
    private final EmbeddingModel embeddingModel;
    private final String WEAVIATE_API_KEY = System.getenv("WEAVIATE_API_KEY");
    private final String WEAVIATE_HOST = System.getenv("WEAVIATE_HOST");


    private final Map<String, WeaviateEmbeddingStore> embeddingStores = new HashMap<>();

    public IngestService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        PoliticalParties.parties.forEach((key, value) -> {
            WeaviateEmbeddingStore embeddingStore = WeaviateEmbeddingStore.builder()
                    .apiKey(WEAVIATE_API_KEY)
                    .host(WEAVIATE_HOST)
                    .scheme("https")
                    .objectClass(key)
                    .build();
            embeddingStores.put(key, embeddingStore);
        });
    }

    public void ingestDocument(Document document, String party) {
        ingestDocuments(List.of(document), party);
    }

    public void ingestDocuments(List<Document> documents, String party) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(1024, 100);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStores.get(party))
                .build();
        ingestor.ingest(documents);
    }
}
