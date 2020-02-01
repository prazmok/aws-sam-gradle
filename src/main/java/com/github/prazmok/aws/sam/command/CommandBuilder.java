package com.github.prazmok.aws.sam.command;

import java.util.LinkedHashSet;

public class CommandBuilder {
    private boolean hasCommand = false;

    protected final LinkedHashSet<String> params = new LinkedHashSet<>();
    protected final ArgSeparator argSeparator;

    public CommandBuilder(ArgSeparator argSeparator) {
        this.argSeparator = argSeparator;
    }

    public CommandBuilder command(String command) {
        if (!hasCommand) {
            params.add(command);
            hasCommand = true;
        }

        return this;
    }

    public CommandBuilder task(String task) {
        params.add(task);

        return this;
    }

    public CommandBuilder argument(String argument, Object value) {
        if (value != null) {
            String val = value.toString();

            if (!val.isEmpty()) {
                switch (argSeparator) {
                    case EQUAL_SIGN:
                        params.add(argument + "=" + val);
                        break;
                    case SPACE:
                    default:
                        params.add(argument);
                        params.add(val);
                        break;
                }
            }
        }

        return this;
    }

    public CommandBuilder option(String option) {
        params.add(option);

        return this;
    }

    public CommandBuilder option(String option, Boolean value) {
        if (value != null && value) {
            option(option);
        }

        return this;
    }

    final public LinkedHashSet<String> build() throws IllegalStateException {
        if (!hasCommand) {
            throw new IllegalStateException("Missing command property!");
        }

        return params;
    }
}
