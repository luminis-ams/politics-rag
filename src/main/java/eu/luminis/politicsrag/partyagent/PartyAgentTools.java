package eu.luminis.politicsrag.partyagent;

import java.util.List;

import dev.langchain4j.agent.tool.Tool;
import eu.luminis.politicsrag.custom.MultiRetriever;
import eu.luminis.politicsrag.model.PoliticalParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.*;

public class PartyAgentTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyAgentTools.class);

    private final MultiRetriever multiRetriever;

    public PartyAgentTools(MultiRetriever multiRetriever) {
        this.multiRetriever = multiRetriever;
    }

    @Tool
    public String getRelevantDataForPoliticalParty(String question, String party) {
        party = party.substring(0, 1).toUpperCase() + party.substring(1).toLowerCase();
        LOGGER.info("Getting relevant data for party: {}", party);
        return multiRetriever.findRelevant(question, List.of(party)).stream()
                .map(segment -> "...political_party: " + segment.metadata("resource") + " \ntext: " + segment.text() + "...")
                .collect(joining("\n\n"));
    }

    @Tool
    public List<String> getAvailableParties() {
        LOGGER.info("Checking available parties.");
        return PoliticalParties.parties.keySet().stream().toList();
    }
}
