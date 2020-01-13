package com.github.prazmok.aws.sam.task.command;

public class SamCommandBuilder extends CommandBuilder {
    public SamCommandBuilder() {
        super(ArgSeparator.SPACE);
        command("sam");
    }
}
