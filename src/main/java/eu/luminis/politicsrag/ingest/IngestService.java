package eu.luminis.politicsrag.ingest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import eu.luminis.politicsrag.config.RAGConfig;
import eu.luminis.politicsrag.config.keyloader.KeyLoader;
import eu.luminis.politicsrag.model.PoliticalParties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngestService {
    private final EmbeddingModel embeddingModel;

    private final Map<String, WeaviateEmbeddingStore> embeddingStores = new HashMap<>();

    @Autowired
    public IngestService(EmbeddingModel embeddingModel, KeyLoader keyLoader) {
        this.embeddingModel = embeddingModel;
        PoliticalParties.parties.forEach((key, value) -> {
            WeaviateEmbeddingStore embeddingStore = WeaviateEmbeddingStore.builder()
                    .apiKey(keyLoader.getWeaviateAPIKey())
                    .host(keyLoader.getWeaviateURL())
                    .scheme("https")
                    .objectClass(key)
                    .build();
            embeddingStores.put(key, embeddingStore);
        });

        embeddingStores.put("nota", WeaviateEmbeddingStore.builder()
                .apiKey(keyLoader.getWeaviateAPIKey())
                .host(keyLoader.getWeaviateURL())
                .scheme("https")
                .objectClass("Miljoenennota")
                .build());
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

    public void ingestMiljoenenNota(Document document) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(1024, 100);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStores.get("nota"))
                .build();
        ingestor.ingest(document);
    }
}
