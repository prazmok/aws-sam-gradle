package com.github.prazmok.aws.sam.command;

import org.gradle.api.logging.Logger;

import java.util.LinkedHashSet;

public class CommandBuilder {
    private boolean hasCommand = false;

    protected final LinkedHashSet<String> params = new LinkedHashSet<>();
    protected final Logger logger;
    protected final ArgSeparator argSeparator;
    protected final boolean dryRun;

    public CommandBuilder(Logger logger, ArgSeparator argSeparator) {
        this(logger, argSeparator, false);
    }

    public CommandBuilder(Logger logger, ArgSeparator argSeparator, boolean dryRun) {
        this.logger = logger;
        this.argSeparator = argSeparator;
        this.dryRun = dryRun;
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

        if (!dryRun) {
            return params;
        }

        logger.lifecycle("Dry run execution of command:\n\n" + String.join(" ", params));

        return new LinkedHashSet<String>() {{
            add("echo");
        }};
    }
}
