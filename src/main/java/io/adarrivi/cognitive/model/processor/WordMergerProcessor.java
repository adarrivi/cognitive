package io.adarrivi.cognitive.model.processor;

import io.adarrivi.cognitive.model.ThoughUnitType;
import io.adarrivi.cognitive.model.ThoughtUnit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class WordMergerProcessor implements ThoughUnitProcessor {

    private static final Set<String> NEW_WORD_MARKERS = new HashSet<>(asList(" ", "\n", "\t", ",", "."));
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public Optional<ThoughtUnit> process(ThoughtUnit unit) {
        if (ThoughUnitType.INPUT_CHAR != unit.getType()) {
            return empty();
        }
        final String inputChar = unit.getData();
        if (!NEW_WORD_MARKERS.contains(inputChar)) {
            stringBuilder.append(inputChar);
            return empty();
        }
        if (stringBuilder.length() > 0) {
            ThoughtUnit newThoughtUnit = new ThoughtUnit(ThoughUnitType.WORD, stringBuilder.toString());
            stringBuilder.setLength(0);
            return of(newThoughtUnit);
        }
        return empty();
    }
}
