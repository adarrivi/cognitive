package io.adarrivi.cognitive.read;

import io.adarrivi.cognitive.model.ThoughtUnit;
import io.adarrivi.cognitive.model.processor.SentenceMergerProcessor;
import io.adarrivi.cognitive.model.processor.ThoughUnitProcessor;
import io.adarrivi.cognitive.model.processor.WordMergerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.adarrivi.cognitive.model.ThoughUnitType.INPUT_CHAR;
import static io.adarrivi.cognitive.model.ThoughUnitType.SENTENCE;
import static io.adarrivi.cognitive.model.ThoughUnitType.WORD;

public class Reading {

    private static final Logger logger = LoggerFactory.getLogger(Reading.class);

    public static void main(String... args) {
        new Reading().run();
    }


    private void run() {
        final List<ThoughUnitProcessor> processors = Arrays.asList(new WordMergerProcessor(), new SentenceMergerProcessor());
        String fileContent = getFileContent();
        final List<ThoughtUnit> inputThoughtUnits = Arrays.stream(fileContent.split(""))
                .map(inputChar -> new ThoughtUnit(INPUT_CHAR, inputChar))
                .collect(Collectors.toList());
        final List<ThoughtUnit> outputThoughUnits = new ArrayList<>(inputThoughtUnits);

        processors.forEach(processor->{
            final List<ThoughtUnit> newUnits = outputThoughUnits.stream()
                    .map(processor::process)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            outputThoughUnits.addAll(newUnits);
        });

        outputThoughUnits.stream()
                .filter(unit->SENTENCE == unit.getType())
                .map(ThoughtUnit::getData)
                .forEach(sentence -> logger.info("Sentence: {}", sentence));
    }

    private String getFileContent() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/dracula-bramstoker.txt"));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
