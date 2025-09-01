package com.example.brightRassignment.Document;

import java.util.List;

public class Autocomplete {
    private List<String> input;

    public Autocomplete() {}

    public Autocomplete(List<String> input) {
        this.input = input;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }
}
