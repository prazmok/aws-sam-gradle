package com.github.prazmok.aws.sam.task;

import java.util.LinkedHashSet;

class SamCommandBuilder {
    private final LinkedHashSet<String> params = new LinkedHashSet<>();

    SamCommandBuilder() {
        params.add("sam");
    }

    public SamCommandBuilder task(String task) {
        params.add(task);

        return this;
    }

    public SamCommandBuilder argument(String argument, String value) {
        if (value != null && !value.isEmpty()) {
            params.add(argument);
            params.add(value);
        }

        return this;
    }

    public SamCommandBuilder option(String option, Boolean value) {
        if (value != null && value) {
            params.add(option);
        }

        return this;
    }

    public LinkedHashSet<String> build() {
        return params;
    }
}
