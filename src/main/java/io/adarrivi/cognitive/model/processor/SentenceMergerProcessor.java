package io.adarrivi.cognitive.model.processor;

import io.adarrivi.cognitive.model.ThoughUnitType;
import io.adarrivi.cognitive.model.ThoughtUnit;

import java.util.*;

import static io.adarrivi.cognitive.model.ThoughUnitType.*;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class SentenceMergerProcessor implements ThoughUnitProcessor {

    private static final Set<String> NEW_SENTENCE_MARKER = new HashSet<>(asList("."));
    private List<String> words = new ArrayList<>();

    @Override
    public Optional<ThoughtUnit> process(ThoughtUnit unit) {

        final ThoughUnitType type = unit.getType();
        final String data = unit.getData();
        if (WORD == type) {
            words.add(data);
            return empty();
        }

        if (INPUT_CHAR == type && NEW_SENTENCE_MARKER.contains(data) && !words.isEmpty()) {
            final ThoughtUnit newThoughtUnit = new ThoughtUnit(SENTENCE, String.join(" ", words));
            words.clear();
            return of(newThoughtUnit);
        }
        return empty();
    }
}
