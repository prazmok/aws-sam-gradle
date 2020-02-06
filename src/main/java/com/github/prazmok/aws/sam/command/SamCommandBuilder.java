package com.github.prazmok.aws.sam.command;

import org.gradle.api.logging.Logger;

public class SamCommandBuilder extends CommandBuilder {
    public SamCommandBuilder(Logger logger, boolean dryRun) {
        super(logger, ArgSeparator.SPACE, dryRun);
        command("sam");
    }
}
