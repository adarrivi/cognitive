package io.adarrivi.cognitive.model;

public class ThoughtUnit {

    private ThoughUnitType type;
    private String data;

    public ThoughtUnit(ThoughUnitType type, String data) {
        this.type = type;
        this.data = data;
    }

    public ThoughUnitType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}