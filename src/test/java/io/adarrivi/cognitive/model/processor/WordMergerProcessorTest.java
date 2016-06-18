package io.adarrivi.cognitive.model.processor;


import io.adarrivi.cognitive.model.ThoughUnitType;
import io.adarrivi.cognitive.model.ThoughtUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class WordMergerProcessorTest {

    private WordMergerProcessor victim;
    private List<ThoughtUnit> inputThoughtUnits;
    private List<ThoughtUnit> outputThoughtUnits;


    @DataProvider
    public Object[][] inputChars() {
        return new Object[][]{
                {"Dracula.", asList("Dracula")},
                {"Count Dracula.", asList("Count", "Dracula")},
                {"Hotel, which I.", asList("Hotel", "which", "I")},
                {"Carpathians. I am ", asList("Carpathians", "I", "am")},
                {"Carpathians\n I am ", asList("Carpathians", "I", "am")},
        };
    }


    @Test(dataProvider = "inputChars")
    public void process_ReturnsProperlySeparatedWords(final String inputString, final List<String> expectedWords) {
        givenInputThoughUnits(inputString);
        whenProcessAll();
        thenOutputWordsShouldBe(expectedWords);
    }

    private void givenInputThoughUnits(final String inputString) {
        inputThoughtUnits = Arrays.stream(inputString.split(""))
                .map(inputChar -> new ThoughtUnit(ThoughUnitType.INPUT_CHAR, inputChar))
                .collect(toList());
    }

    private void whenProcessAll() {
        victim = new WordMergerProcessor();
        outputThoughtUnits = inputThoughtUnits.stream()
                .map(unit -> victim.process(unit))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private void thenOutputWordsShouldBe(final List<String> expectedWords) {
        final List<String> processedWords = outputThoughtUnits.stream()
                .filter(unit->ThoughUnitType.WORD == unit.getType())
                .map(ThoughtUnit::getData)
                .collect(Collectors.toList());
        assertThat(processedWords, equalTo(expectedWords));
    }


}