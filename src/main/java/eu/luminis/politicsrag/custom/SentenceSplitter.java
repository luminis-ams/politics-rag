package eu.luminis.politicsrag.custom;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SentenceSplitter implements DocumentSplitter {
    @Override
    public List<TextSegment> split(Document document) {
        String text = document.text();
        SentenceDetectorME sentenceDetector = createSentenceDetector();
        String[] sentences = sentenceDetector.sentDetect(text);

        // TODO verify sentence length, split in case the sentence is too long

        document.metadata().add("num_sentences", String.valueOf(sentences.length));

        List<TextSegment> segments = new ArrayList<>();
        for (int i = 0; i < sentences.length; i++) {
            segments.add(createSegment(sentences[i], document, i));
        }
        return segments;
    }

    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        return DocumentSplitter.super.splitAll(documents);
    }

    private SentenceDetectorME createSentenceDetector() {
        String sentenceModelFilePath = "/opennlp/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin";
        try (InputStream is = getClass().getResourceAsStream(sentenceModelFilePath)) {
            return new SentenceDetectorME(new SentenceModel(Objects.requireNonNull(is)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TextSegment createSegment(String text, Document document, int index) {
        Metadata metadata = document.metadata().copy().add("index", String.valueOf(index));
        return TextSegment.from(text, metadata);
    }


}
