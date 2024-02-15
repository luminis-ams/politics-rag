package eu.luminis.politicsrag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import eu.luminis.politicsrag.custom.SentenceSplitter;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SplitterTest {
    @Test
    public void checkSentenceSplitter() {
        String text = "This is a sentence. This is another sentence. A third sentence with a € 6.20 number and a dot int it";
        Document document = Document.from(text, Metadata.from("doc_id", "my_first_document"));
        SentenceSplitter sentenceSplitter = new SentenceSplitter();
        List<TextSegment> sentences = sentenceSplitter.split(document);
        for (TextSegment sentence : sentences) {
            System.out.println(sentence.metadata().get("doc_id"));
            System.out.println(sentence.metadata().get("index"));
            System.out.println(sentence.metadata().get("num_sentences"));
            System.out.println(sentence.text());
        }
    }
}
