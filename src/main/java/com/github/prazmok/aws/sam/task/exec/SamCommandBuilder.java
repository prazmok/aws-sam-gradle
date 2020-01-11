package com.github.prazmok.aws.sam.task.exec;

import java.util.LinkedHashSet;

public class SamCommandBuilder {
    private final LinkedHashSet<String> params = new LinkedHashSet<>();

    public SamCommandBuilder() {
        params.add("sam");
    }

    public SamCommandBuilder task(String task) {
        params.add(task);

        return this;
    }

    public SamCommandBuilder argument(String argument, Object value) {
        if (value != null) {
            String val = value.toString();

            if (!val.isEmpty()) {
                params.add(argument);
                params.add(val);
            }
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
