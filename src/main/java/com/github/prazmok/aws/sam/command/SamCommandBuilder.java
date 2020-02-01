package com.github.prazmok.aws.sam.command;

public class SamCommandBuilder extends CommandBuilder {
    public SamCommandBuilder() {
        super(ArgSeparator.SPACE);
        command("sam");
    }
}
