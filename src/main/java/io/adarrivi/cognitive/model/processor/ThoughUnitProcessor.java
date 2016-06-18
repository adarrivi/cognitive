package io.adarrivi.cognitive.model.processor;

import io.adarrivi.cognitive.model.ThoughtUnit;

import java.util.Optional;

public interface ThoughUnitProcessor {

    Optional<ThoughtUnit> process(ThoughtUnit unit);

}
